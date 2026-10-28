package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.app.apikey.domain.ApiKey;
import it.chalmers.gamma.app.apikey.domain.ApiKeyId;
import it.chalmers.gamma.app.apikey.domain.ApiKeyToken;
import it.chalmers.gamma.app.apikey.domain.ApiKeyType;
import it.chalmers.gamma.app.client.domain.*;
import it.chalmers.gamma.app.client.domain.authority.Authority;
import it.chalmers.gamma.app.client.domain.authority.AuthorityName;
import it.chalmers.gamma.app.client.domain.authority.ClientAuthorityRepository;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.common.Text;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupId;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupRepository;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class ClientBootstrap {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClientBootstrap.class);

  private final MockData mockData;
  private final ClientRepository clientRepository;
  private final ClientAuthorityRepository clientAuthorityRepository;
  private final SuperGroupRepository superGroupRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final BootstrapSettings bootstrapSettings;

  public ClientBootstrap(
      MockData mockData,
      ClientRepository clientRepository,
      ClientAuthorityRepository clientAuthorityRepository,
      SuperGroupRepository superGroupRepository,
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      BootstrapSettings bootstrapSettings) {
    this.mockData = mockData;
    this.clientRepository = clientRepository;
    this.clientAuthorityRepository = clientAuthorityRepository;
    this.superGroupRepository = superGroupRepository;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.bootstrapSettings = bootstrapSettings;
  }

  public void createClients() {
    if (!this.bootstrapSettings.mocking()
        || this.mockData.clients().isEmpty()
        || !this.clientRepository.getAll().isEmpty()) {
      return;
    }

    LOGGER.info("========== CLIENT BOOTSTRAP ==========");

    for (MockData.MockClient mockClient : this.mockData.clients()) {
      Client client = toClient(mockClient);
      this.clientRepository.save(client);

      for (MockData.MockClientAuthority mockAuthority : mockClient.authorities()) {
        createAuthority(client, mockAuthority);
      }

      LOGGER.info(
          "Client "
              + mockClient.prettyName()
              + " has been created with client id: "
              + mockClient.clientId()
              + ", client secret: "
              + mockClient.clientSecret()
              + " and redirect url: "
              + mockClient.redirectUrl());

      if (mockClient.apiKey() != null) {
        LOGGER.info(
            "Client "
                + mockClient.prettyName()
                + " has the client api key with id: "
                + mockClient.apiKey().id()
                + " and token: "
                + mockClient.apiKey().token());
      }
    }

    LOGGER.info("==========                  ==========");
  }

  private Client toClient(MockData.MockClient mockClient) {
    ApiKey apiKey = null;
    if (mockClient.apiKey() != null) {
      apiKey =
          new ApiKey(
              new ApiKeyId(mockClient.apiKey().id()),
              new PrettyName(mockClient.prettyName()),
              new Text(
                  "Api nyckel för klienten: " + mockClient.prettyName(),
                  "Api key for client: " + mockClient.prettyName()),
              ApiKeyType.CLIENT,
              new ApiKeyToken(this.passwordEncoder.encode(mockClient.apiKey().token())));
    }

    List<Scope> scopes = new ArrayList<>();
    scopes.add(Scope.PROFILE);
    if (mockClient.emailScope()) {
      scopes.add(Scope.EMAIL);
    }

    ClientOwner owner =
        mockClient.ownerId() == null
            ? new ClientOwnerOfficial()
            : new ClientUserOwner(new UserId(mockClient.ownerId()));

    return new Client(
        new ClientUid(mockClient.id()),
        new ClientId(mockClient.clientId()),
        new ClientSecret(this.passwordEncoder.encode(mockClient.clientSecret())),
        new ClientRedirectUrl(mockClient.redirectUrl()),
        new PrettyName(mockClient.prettyName()),
        new Text(mockClient.description().sv(), mockClient.description().en()),
        scopes,
        apiKey,
        owner,
        null);
  }

  private void createAuthority(Client client, MockData.MockClientAuthority mockAuthority) {
    AuthorityName name = new AuthorityName(mockAuthority.name());

    try {
      this.clientAuthorityRepository.create(client.clientUid(), name);
    } catch (ClientAuthorityRepository.ClientAuthorityAlreadyExistsException e) {
      LOGGER.error("Error creating client authority: " + mockAuthority.name() + ";");
      return;
    }

    this.clientAuthorityRepository.save(
        new Authority(
            client,
            name,
            mockAuthority.superGroupIds().stream()
                .map(id -> this.superGroupRepository.get(new SuperGroupId(id)).orElseThrow())
                .toList(),
            mockAuthority.userIds().stream()
                .map(id -> this.userRepository.get(new UserId(id)).orElseThrow())
                .toList()));
  }
}
