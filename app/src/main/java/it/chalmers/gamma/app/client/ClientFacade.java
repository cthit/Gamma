package it.chalmers.gamma.app.client;

import static it.chalmers.gamma.app.authentication.AccessGuard.*;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.apikey.ApiKeyFacade;
import it.chalmers.gamma.app.apikey.domain.ApiKey;
import it.chalmers.gamma.app.apikey.domain.ApiKeyId;
import it.chalmers.gamma.app.apikey.domain.ApiKeyToken;
import it.chalmers.gamma.app.apikey.domain.ApiKeyType;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.client.domain.*;
import it.chalmers.gamma.app.client.domain.restriction.ClientRestriction;
import it.chalmers.gamma.app.client.domain.restriction.ClientRestrictionId;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.common.Text;
import it.chalmers.gamma.app.group.domain.GroupRepository;
import it.chalmers.gamma.app.supergroup.SuperGroupFacade;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupId;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupRepository;
import it.chalmers.gamma.app.user.UserFacade;
import it.chalmers.gamma.app.user.domain.GammaUser;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserMembership;
import it.chalmers.gamma.app.user.domain.UserRepository;
import it.chalmers.gamma.security.authentication.AuthenticationExtractor;
import it.chalmers.gamma.security.authentication.UserAuthentication;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ClientFacade extends Facade {

  private final ClientRepository clientRepository;
  private final SuperGroupRepository superGroupRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final GroupRepository groupRepository;

  public ClientFacade(
          AccessGuard accessGuard,
          ClientRepository clientRepository,
          SuperGroupRepository superGroupRepository,
          UserRepository userRepository,
          PasswordEncoder passwordEncoder,
          GroupRepository groupRepository) {
    super(accessGuard);
    this.clientRepository = clientRepository;
    this.superGroupRepository = superGroupRepository;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.groupRepository = groupRepository;
  }

  @Transactional
  public CreatedClientDTO createOfficialClient(NewClient newClient) {
    this.accessGuard.require(isAdmin());

    return this.create(newClient, new ClientOwnerOfficial());
  }

  @Transactional
  public CreatedClientDTO createUserClient(NewClient newClient) {
    this.accessGuard.require(isSignedIn());

    if (newClient.restrictions != null) {
      throw new IllegalArgumentException("user client cannot have restrictions");
    }

    if (AuthenticationExtractor.getAuthentication()
        instanceof UserAuthentication userAuthentication) {
      return this.create(newClient, new ClientUserOwner(userAuthentication.gammaUser().id()));
    }

    throw new IllegalStateException();
  }

  private CreatedClientDTO create(NewClient newClient, ClientOwner clientOwner) {
    ClientSecret.GeneratedClientSecret generatedClientSecret =
        ClientSecret.generate(passwordEncoder);
    ApiKey apiKey = null;
    ApiKeyToken.GeneratedApiKeyToken generatedApiKeyToken = null;

    if (newClient.generateApiKey) {
      generatedApiKeyToken = ApiKeyToken.generate(passwordEncoder);

      apiKey =
          new ApiKey(
              ApiKeyId.generate(),
              new PrettyName(newClient.prettyName),
              new Text(
                  "Api nyckel för klienten: " + newClient.prettyName,
                  "Api key for client: " + newClient.prettyName),
              ApiKeyType.CLIENT,
              generatedApiKeyToken.apiKeyToken());
    }

    List<Scope> scopes = new ArrayList<>();
    scopes.add(Scope.PROFILE);
    if (newClient.emailScope) {
      scopes.add(Scope.EMAIL);
    }

    ClientUid clientUid = ClientUid.generate();
    ClientId clientId = ClientId.generate();

    ClientRestriction restrictions = null;
    if (newClient.restrictions != null) {
      restrictions =
          new ClientRestriction(
              ClientRestrictionId.generate(),
              newClient.restrictions.superGroups.stream()
                  .map(
                      superGroupId ->
                          this.superGroupRepository
                              .get(new SuperGroupId(superGroupId))
                              .orElseThrow())
                  .toList());
    }

    Client client =
        new Client(
            clientUid,
            clientId,
            generatedClientSecret.clientSecret(),
            new ClientRedirectUrl(newClient.redirectUrl),
            new PrettyName(newClient.prettyName),
            new Text(newClient.svDescription, newClient.enDescription),
            scopes,
            apiKey,
            clientOwner,
            restrictions);

    this.clientRepository.save(client);

    return new CreatedClientDTO(
        createDTO(client),
        generatedClientSecret.rawSecret(),
        generatedApiKeyToken == null ? null : generatedApiKeyToken.rawToken());
  }

  public void delete(UUID clientUid) throws ClientFacade.ClientNotFoundException {
    ClientUid uid = new ClientUid(clientUid);

    this.accessGuard.requireEither(isAdmin(), ownerOfClient(uid));

    try {
      this.clientRepository.delete(uid);
    } catch (ClientRepository.ClientNotFoundException e) {
      throw new ClientFacade.ClientNotFoundException();
    }
  }

  public Optional<ClientDTO> get(UUID clientUid) {
    ClientUid uid = new ClientUid(clientUid);

    this.accessGuard.requireEither(isAdmin(), ownerOfClient(uid));

    return this.clientRepository.get(uid).map(this::createDTO);
  }

  public List<ClientDTO> getAll() {
    this.accessGuard.require(isAdmin());

    return this.clientRepository.getAll().stream().map(this::createDTO).toList();
  }

  public List<ClientDTO> getAllMyClients() {
    this.accessGuard.require(isSignedIn());

    if (AuthenticationExtractor.getAuthentication()
        instanceof UserAuthentication userAuthentication) {
      return this.clientRepository.getAllUserClients(userAuthentication.gammaUser().id()).stream()
          .map(this::createDTO)
          .toList();
    }

    throw new IllegalStateException();
  }

  public String resetClientSecret(UUID clientUid) throws ClientNotFoundException {
    ClientUid uid = new ClientUid(clientUid);

    this.accessGuard.requireEither(isAdmin(), ownerOfClient(uid));

    Client client = this.clientRepository.get(uid).orElseThrow(ClientNotFoundException::new);
    ClientSecret.GeneratedClientSecret generated = ClientSecret.generate(passwordEncoder);

    Client newClient = client.withClientSecret(generated.clientSecret());

    this.clientRepository.save(newClient);

    return generated.rawSecret();
  }

  public Optional<UserFacade.UserDTO> getClientOwner(String clientId) {
    accessGuard.require(isSignedIn());

    Client client = this.clientRepository.get(new ClientId(clientId)).orElseThrow();

    if (client.owner() instanceof ClientUserOwner(UserId userId)) {
      return this.userRepository.get(userId).map(UserFacade.UserDTO::new);
    }

    return Optional.empty();
  }

  public boolean hasAccessToClient(String clientId) {
    accessGuard.require(isSignedIn());

    if (AuthenticationExtractor.getAuthentication()
            instanceof UserAuthentication userAuthentication) {
      GammaUser user = userAuthentication.gammaUser();
      Client client = this.clientRepository.get(new ClientId(clientId)).orElseThrow();

      if (client.restrictions().isEmpty()) {
        return true;
      }

      List<UserMembership> memberships = this.groupRepository.getAllByUser(user.id());
      List<SuperGroupId> userSuperGroups =
              memberships.stream()
                      .map(UserMembership::group)
                      .map(group -> group.superGroup().id())
                      .distinct()
                      .toList();

      return client.restrictions().get().superGroups().stream()
              .anyMatch(superGroup -> userSuperGroups.contains(superGroup.id()));
    }

    return false;
  }

  public record NewClientRestrictions(List<UUID> superGroups) {}

  public record NewClient(
      String redirectUrl,
      String prettyName,
      String svDescription,
      String enDescription,
      boolean generateApiKey,
      boolean emailScope,
      NewClientRestrictions restrictions) {}

  public record CreatedClientDTO(ClientDTO client, String clientSecret, String apiKeyToken) {}

  private ClientDTO createDTO(Client client) {
    return new ClientDTO(
        client.clientUid().value(),
        client.clientId().value(),
        client.clientRedirectUrl().value(),
        client.prettyName().value(),
        client.description().sv().value(),
        client.description().en().value(),
        client.clientApiKey().map(ApiKeyFacade.ApiKeyDTO::new),
        client
            .restrictions()
            .map(
                clientRestriction ->
                    new ClientDTO.ClientRestrictionDTO(
                        clientRestriction.superGroups().stream()
                            .map(SuperGroupFacade.SuperGroupDTO::new)
                            .toList()))
            .orElse(null),
        convert(client.owner()));
  }

  private ClientDTO.Owner convert(ClientOwner clientOwner) {
    return switch (clientOwner) {
      case ClientOwnerOfficial() -> new ClientDTO.OfficialOwner();
      case ClientUserOwner(UserId userId) ->
          new ClientDTO.UserOwner(
              new UserFacade.UserDTO(this.userRepository.get(userId).orElseThrow()));
    };
  }

  public record ClientDTO(
      UUID clientUid,
      String clientId,
      String webServerRedirectUrl,
      String prettyName,
      String svDescription,
      String enDescription,
      Optional<ApiKeyFacade.ApiKeyDTO> apiKey,
      ClientRestrictionDTO restriction,
      Owner owner) {

    public record ClientRestrictionDTO(List<SuperGroupFacade.SuperGroupDTO> superGroups) {}

    public sealed interface Owner permits OfficialOwner, UserOwner {}

    public record OfficialOwner() implements Owner {}

    public record UserOwner(UserFacade.UserDTO user) implements Owner {}
  }

  public static class ClientNotFoundException extends Exception {}
}
