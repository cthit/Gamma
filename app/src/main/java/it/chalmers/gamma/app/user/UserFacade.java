package it.chalmers.gamma.app.user;

import static it.chalmers.gamma.app.authentication.AccessGuard.*;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.apikey.domain.ApiKeyType;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.client.domain.Client;
import it.chalmers.gamma.app.client.domain.approval.ClientApprovalsRepository;
import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.datadeletion.DataDeletionRecorder;
import it.chalmers.gamma.app.group.GroupFacade;
import it.chalmers.gamma.app.group.domain.GroupRepository;
import it.chalmers.gamma.app.post.PostFacade;
import it.chalmers.gamma.app.signin.domain.RecentSignIn;
import it.chalmers.gamma.app.user.domain.*;
import it.chalmers.gamma.app.user.emailchange.domain.EmailChangeRepository;
import it.chalmers.gamma.security.authentication.ApiAuthentication;
import it.chalmers.gamma.security.authentication.AuthenticationExtractor;
import it.chalmers.gamma.security.authentication.UserAuthentication;
import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class UserFacade extends Facade {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserFacade.class);

  private final UserRepository userRepository;
  private final GroupRepository groupRepository;
  private final ClientApprovalsRepository clientApprovalsRepository;
  private final DataDeletionRecorder dataDeletionRecorder;
  private final EmailChangeRepository emailChangeRepository;
  private final RecentSignIn recentSignIn;
  private final ApplicationEventPublisher events;

  public UserFacade(
      AccessGuard accessGuard,
      UserRepository userRepository,
      GroupRepository groupRepository,
      ClientApprovalsRepository clientApprovalsRepository,
      DataDeletionRecorder dataDeletionRecorder,
      EmailChangeRepository emailChangeRepository,
      RecentSignIn recentSignIn,
      ApplicationEventPublisher events) {
    super(accessGuard);
    this.userRepository = userRepository;
    this.groupRepository = groupRepository;
    this.clientApprovalsRepository = clientApprovalsRepository;
    this.dataDeletionRecorder = dataDeletionRecorder;
    this.emailChangeRepository = emailChangeRepository;
    this.recentSignIn = recentSignIn;
    this.events = events;
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

  @Transactional
  public void deleteUser(UUID id) {
    accessGuard.require(isAdmin());

    try {
      this.userRepository
          .get(new UserId(id))
          .ifPresent(this.dataDeletionRecorder::recordAccountDeletion);
      this.userRepository.delete(new UserId(id));
    } catch (UserRepository.UserNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * Locks or unlocks a user account. A locked user can't sign in, is signed out of their current
   * sessions, can't authorize clients and is hidden from everyone but admins.
   */
  public void setLocked(UUID id, boolean locked) throws CannotLockYourselfException {
    accessGuard.require(isAdmin());

    UserId userId = new UserId(id);

    if (locked
        && AuthenticationExtractor.getAuthentication() instanceof UserAuthentication signedIn
        && signedIn.gammaUser().id().equals(userId)) {
      throw new CannotLockYourselfException();
    }

    this.userRepository.setLocked(userId, locked);
  }

  public static class CannotLockYourselfException extends Exception {}

  public Optional<UserExtendedWithGroupsDTO> getAsAdmin(UUID id) {
    return getExtendedAsAdmin(id)
        .map(userExtendedDTO ->
            new UserExtendedWithGroupsDTO(userExtendedDTO, getUserGroups(new UserId(id))));
  }

  public Optional<UserExtendedDTO> getExtendedAsAdmin(UUID id) {
    accessGuard.require(isAdmin());

    return this.userRepository.get(new UserId(id)).map(UserExtendedDTO::new);
  }

  @Transactional
  public void updateUser(UpdateUser updateUser) {
    accessGuard.require(isAdmin());

    UserId userId = new UserId(updateUser.id);
    // Serializes with recovery and credential changes, so the email can't change under them.
    this.userRepository.lockSignInMethods(userId);
    GammaUser oldUser = this.userRepository.get(userId).orElseThrow();
    Email previousEmail = oldUser.extended().email();
    Email newEmail = new Email(updateUser.email);
    boolean emailChanged = !newEmail.value().equalsIgnoreCase(previousEmail.value());

    // A new email would let the forgot password flow mail a reset link to it, so it needs the same
    // recent confirmation as admin recovery.
    if (emailChanged && !this.recentSignIn.isRecent()) {
      throw new AccessGuard.AccessDeniedException();
    }

    this.userRepository.save(
        oldUser
            .with()
            .nick(new Nick(updateUser.nick))
            .firstName(new FirstName(updateUser.firstName))
            .lastName(new LastName(updateUser.lastName))
            .language(Language.valueOf(updateUser.language))
            .acceptanceYear(new AcceptanceYear(updateUser.acceptanceYear))
            .extended(oldUser.extended().with().email(newEmail).build())
            .build());

    if (emailChanged) {
      this.userRepository.requireEmailRecoveryVerification(userId);
      this.emailChangeRepository.delete(userId);
      LOGGER.warn(
          "Admin {} changed the email of the user {}; email recovery is off until it is verified",
          actingUserId(),
          userId.value());
      this.events.publishEvent(new EmailChangedByAdmin(userId, previousEmail));
    }
  }

  private static UUID actingUserId() {
    if (AuthenticationExtractor.getAuthentication() instanceof UserAuthentication signedIn) {
      return signedIn.gammaUser().id().value();
    }
    return null;
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
