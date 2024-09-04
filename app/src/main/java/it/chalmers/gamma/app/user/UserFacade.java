package it.chalmers.gamma.app.user;

import static it.chalmers.gamma.app.authentication.AccessGuard.*;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.apikey.domain.ApiKeyType;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.client.domain.Client;
import it.chalmers.gamma.app.client.domain.approval.ClientApprovalsRepository;
import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.group.GroupFacade;
import it.chalmers.gamma.app.group.domain.GroupRepository;
import it.chalmers.gamma.app.post.PostFacade;
import it.chalmers.gamma.app.user.domain.*;
import it.chalmers.gamma.security.authentication.ApiAuthentication;
import it.chalmers.gamma.security.authentication.AuthenticationExtractor;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UserFacade extends Facade {

  private final UserRepository userRepository;
  private final GroupRepository groupRepository;
  private final ClientApprovalsRepository clientApprovalsRepository;

  public UserFacade(
      AccessGuard accessGuard,
      UserRepository userRepository,
      GroupRepository groupRepository,
      ClientApprovalsRepository clientApprovalsRepository) {
    super(accessGuard);
    this.userRepository = userRepository;
    this.groupRepository = groupRepository;
    this.clientApprovalsRepository = clientApprovalsRepository;
  }

  public Optional<UserDTO> get(UUID id) {
    UserId userId = new UserId(id);
    accessGuard.requireEither(isSignedIn(), userHasAcceptedClient(userId), isApi(ApiKeyType.INFO));

    return this.userRepository.get(userId).map(UserDTO::new);
  }

  public Optional<UserWithGroupsDTO> getWithGroups(UUID id) {
    UserId userId = new UserId(id);
    accessGuard.requireEither(isSignedIn(), userHasAcceptedClient(userId), isApi(ApiKeyType.INFO));

    Optional<UserDTO> maybeUser = this.userRepository.get(userId).map(UserDTO::new);
    return maybeUser.map(userDTO -> new UserWithGroupsDTO(userDTO, getUserGroups(userId)));
  }

  private List<UserGroupDTO> getUserGroups(UserId userId) {
    return this.groupRepository.getAllByUser(userId).stream().map(UserGroupDTO::new).toList();
  }

  public List<UserDTO> getAll() {
    accessGuard.require(isSignedIn());

    return this.userRepository.getAll().stream().map(UserDTO::new).toList();
  }

  public List<UserDTO> getAllByClientAccepting() {
    this.accessGuard.require(isClientApi());

    if (AuthenticationExtractor.getAuthentication()
        instanceof ApiAuthentication apiAuthentication) {
      Client client = apiAuthentication.getClient().orElseThrow();
      return clientApprovalsRepository.getAllByClientUid(client.clientUid()).stream()
          .map(UserDTO::new)
          .toList();
    }

    return Collections.emptyList();
  }

  public void setUserPassword(UUID id, String newPassword) {
    accessGuard.require(isAdmin());
    this.userRepository.setPassword(new UserId(id), new UnencryptedPassword(newPassword));
  }

  public void deleteUser(UUID id) {
    accessGuard.require(isAdmin());

    try {
      this.userRepository.delete(new UserId(id));
    } catch (UserRepository.UserNotFoundException e) {
      e.printStackTrace();
    }
  }

  public Optional<UserExtendedWithGroupsDTO> getAsAdmin(UUID id) {
    accessGuard.require(isAdmin());

    UserId userId = new UserId(id);

    Optional<UserExtendedDTO> maybeUser = this.userRepository.get(userId).map(UserExtendedDTO::new);
    return maybeUser.map(
        userExtendedDTO -> new UserExtendedWithGroupsDTO(userExtendedDTO, getUserGroups(userId)));
  }

  public void updateUser(UpdateUser updateUser) {
    accessGuard.require(isAdmin());

    GammaUser oldUser = this.userRepository.get(new UserId(updateUser.id)).orElseThrow();
    this.userRepository.save(
        oldUser
            .with()
            .nick(new Nick(updateUser.nick))
            .firstName(new FirstName(updateUser.firstName))
            .lastName(new LastName(updateUser.lastName))
            .language(Language.valueOf(updateUser.language))
            .acceptanceYear(new AcceptanceYear(updateUser.acceptanceYear))
            .extended(oldUser.extended().with().email(new Email(updateUser.email)).build())
            .build());
  }

  public record UserDTO(
      String cid, String nick, String firstName, String lastName, UUID id, int acceptanceYear) {

    public UserDTO(GammaUser user) {
      this(
          user.cid().value(),
          user.nick().value(),
          user.firstName().value(),
          user.lastName().value(),
          user.id().value(),
          user.acceptanceYear().value());
    }

    public String fullName() {
      return firstName + " '" + nick + "' " + lastName;
    }
  }

  public record UserGroupDTO(GroupFacade.GroupDTO group, PostFacade.PostDTO post) {
    public UserGroupDTO(UserMembership userMembership) {
      this(
          new GroupFacade.GroupDTO(userMembership.group()),
          new PostFacade.PostDTO(userMembership.post()));
    }
  }

  public record UserWithGroupsDTO(UserDTO user, List<UserGroupDTO> groups) {}

  public record UserExtendedDTO(
      String cid,
      String nick,
      String firstName,
      String lastName,
      UUID id,
      int version,
      int acceptanceYear,
      String email,
      boolean locked,
      String language) {

    public UserExtendedDTO(GammaUser user) {
      this(
          user.cid().value(),
          user.nick().value(),
          user.firstName().value(),
          user.lastName().value(),
          user.id().value(),
          user.extended().version(),
          user.acceptanceYear().value(),
          user.extended().email().value(),
          user.extended().locked(),
          user.language().name());
    }
  }

  public record UserExtendedWithGroupsDTO(UserExtendedDTO user, List<UserGroupDTO> groups) {}

  public record UpdateUser(
      UUID id,
      String nick,
      String firstName,
      String lastName,
      String email,
      String language,
      int acceptanceYear) {}
}
