package it.chalmers.gamma.app.user;

import static it.chalmers.gamma.app.authentication.AccessGuard.isSignedIn;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.client.domain.Client;
import it.chalmers.gamma.app.client.domain.ClientOwnerOfficial;
import it.chalmers.gamma.app.client.domain.ClientRepository;
import it.chalmers.gamma.app.client.domain.ClientUid;
import it.chalmers.gamma.app.client.domain.ClientUserOwner;
import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.datadeletion.DataDeletionRecorder;
import it.chalmers.gamma.app.datadeletion.domain.DataDeletionRequest;
import it.chalmers.gamma.app.datadeletion.domain.DataDeletionRequestRepository;
import it.chalmers.gamma.app.group.GroupFacade;
import it.chalmers.gamma.app.group.domain.GroupRepository;
import it.chalmers.gamma.app.image.domain.Image;
import it.chalmers.gamma.app.image.domain.ImageService;
import it.chalmers.gamma.app.image.domain.ImageUri;
import it.chalmers.gamma.app.oauth2.domain.GammaAuthorizationRepository;
import it.chalmers.gamma.app.post.PostFacade;
import it.chalmers.gamma.app.signin.SignInChanged;
import it.chalmers.gamma.app.signin.domain.RecentSignIn;
import it.chalmers.gamma.app.user.domain.*;
import it.chalmers.gamma.security.authentication.AuthenticationExtractor;
import it.chalmers.gamma.security.authentication.GammaAuthentication;
import it.chalmers.gamma.security.authentication.UserAuthentication;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class MeFacade extends Facade {

  private static final Logger LOGGER = LoggerFactory.getLogger(MeFacade.class);

  private final UserRepository userRepository;
  private final ClientRepository clientRepository;
  private final GroupRepository groupRepository;
  private final ImageService imageService;
  private final RecentSignIn recentSignIn;
  private final ApplicationEventPublisher events;
  private final GammaAuthorizationRepository authorizations;
  private final DataDeletionRecorder dataDeletionRecorder;
  private final DataDeletionRequestRepository dataDeletionRequestRepository;

  public MeFacade(
      AccessGuard accessGuard,
      UserRepository userRepository,
      ClientRepository clientRepository,
      GroupRepository groupRepository,
      ImageService imageService,
      RecentSignIn recentSignIn,
      ApplicationEventPublisher events,
      GammaAuthorizationRepository authorizations,
      DataDeletionRecorder dataDeletionRecorder,
      DataDeletionRequestRepository dataDeletionRequestRepository) {
    super(accessGuard);
    this.recentSignIn = recentSignIn;
    this.events = events;
    this.userRepository = userRepository;
    this.clientRepository = clientRepository;
    this.groupRepository = groupRepository;
    this.imageService = imageService;
    this.authorizations = authorizations;
    this.dataDeletionRecorder = dataDeletionRecorder;
    this.dataDeletionRequestRepository = dataDeletionRequestRepository;
  }

  public List<UserApprovedClientDTO> getSignedInUserApprovals() {
    this.accessGuard.require(isSignedIn());

    if (AuthenticationExtractor.getAuthentication()
        instanceof UserAuthentication userAuthentication) {
      GammaUser user = userAuthentication.gammaUser();
      return this.clientRepository.getClientsByUserApproved(user.id()).stream()
          .map(UserApprovedClientDTO::new)
          .toList();
    } else {
      return null;
    }
  }

  @Transactional
  public void deleteUserApproval(UUID clientUid) {
    this.accessGuard.require(isSignedIn());

    if (AuthenticationExtractor.getAuthentication()
        instanceof UserAuthentication userAuthentication) {
      GammaUser me = userAuthentication.gammaUser();
      this.dataDeletionRecorder.recordApprovalRevoked(me, new ClientUid(clientUid));
      if (this.clientRepository.isApprovedByUser(me.id(), new ClientUid(clientUid))) {
        this.clientRepository
            .get(new ClientUid(clientUid))
            .filter(client -> client.owner() instanceof ClientUserOwner)
            .ifPresent(client ->
                this.clientRepository.rememberWithdrawnContact(me.id(), client.clientUid()));
      }
      this.clientRepository.deleteUserApproval(new ClientUid(clientUid), me.id());
      this.authorizations.revokeForUser(me.id().value().toString(), clientUid.toString());
    }
  }

  public List<UserApprovedClientDTO> getWithdrawnContacts() {
    this.accessGuard.require(isSignedIn());
    if (AuthenticationExtractor.getAuthentication() instanceof UserAuthentication signedIn) {
      return this.clientRepository.getWithdrawnContacts(signedIn.gammaUser().id()).stream()
          .map(UserApprovedClientDTO::new)
          .toList();
    }
    throw new IllegalCallerException();
  }

  public void dismissWithdrawnContact(UUID clientUid) {
    this.accessGuard.require(isSignedIn());
    if (AuthenticationExtractor.getAuthentication() instanceof UserAuthentication signedIn) {
      this.clientRepository.dismissWithdrawnContact(
          signedIn.gammaUser().id(), new ClientUid(clientUid));
    }
  }

  public AccountDeletionOverviewDTO getAccountDeletionOverview() {
    this.accessGuard.require(isSignedIn());

    if (!(AuthenticationExtractor.getAuthentication()
        instanceof UserAuthentication userAuthentication)) {
      throw new IllegalCallerException("Can only be called by signed in sessions");
    }

    GammaUser me = userAuthentication.gammaUser();
    List<String> officialClients = new ArrayList<>();
    Map<UserId, OwnerContact> owners = new LinkedHashMap<>();

    for (Client client :
        Stream.concat(
                this.clientRepository.getClientsByUserApproved(me.id()).stream(),
                this.clientRepository.getWithdrawnContacts(me.id()).stream())
            .distinct()
            .toList()) {
      if (client.owner() instanceof ClientOwnerOfficial) {
        officialClients.add(client.prettyName().value());
      } else if (client.owner() instanceof ClientUserOwner(UserId ownerId)) {
        OwnerContact contact = owners.get(ownerId);
        if (contact == null) {
          Optional<Email> email = this.clientRepository.getUserOwnerEmail(client.clientUid());
          if (email.isEmpty()) {
            continue;
          }
          contact =
              new OwnerContact(
                  this.userRepository.get(ownerId).orElse(null), email.get(), new ArrayList<>());
          owners.put(ownerId, contact);
        }
        contact.clients().add(client.prettyName().value());
      }
    }

    List<UnofficialClientOwnerDTO> ownerDTOs =
        owners.values().stream()
            .map(contact ->
                new UnofficialClientOwnerDTO(
                    contact.owner() == null
                        ? contact.email().value()
                        : "%s '%s' %s"
                            .formatted(
                                contact.owner().firstName().value(),
                                contact.owner().nick().value(),
                                contact.owner().lastName().value()),
                    contact.owner() == null ? null : contact.owner().firstName().value(),
                    contact.email().value(),
                    contact.clients().stream().sorted(String::compareToIgnoreCase).toList()))
            .sorted(
                Comparator.comparing(UnofficialClientOwnerDTO::name, String::compareToIgnoreCase))
            .toList();

    return new AccountDeletionOverviewDTO(
        me.id().value(),
        me.cid().value(),
        me.firstName().value(),
        Stream.concat(
                officialClients.stream(),
                this.dataDeletionRequestRepository.getOpenFor(me.id()).stream()
                    .map(DataDeletionRequest::clientName)
                    .map(PrettyName::value))
            .distinct()
            .sorted(String::compareToIgnoreCase)
            .toList(),
        this.dataDeletionRequestRepository.hasClearedFor(me.id()),
        ownerDTOs,
        this.clientRepository.getAllUserClients(me.id()).stream()
            .map(client -> client.prettyName().value())
            .sorted(String::compareToIgnoreCase)
            .toList());
  }

  public MeDTO getMe() {
    GammaAuthentication authenticated = AuthenticationExtractor.getAuthentication();
    GammaUser user = null;
    boolean isAdmin = false;

    if (authenticated instanceof UserAuthentication userAuthentication) {
      user = userAuthentication.gammaUser();
      isAdmin = userAuthentication.isAdmin();
    }

    if (user == null) {
      throw new IllegalCallerException("Can only be called by signed in sessions");
    }

    List<MyMembership> groups =
        this.groupRepository.getAllByUser(user.id()).stream().map(MyMembership::new).toList();

    return new MeDTO(
        user, groups, isAdmin, this.userRepository.emailRecoveryRequiresVerification(user.id()));
  }

  public void updateMe(UpdateMe updateMe) {
    GammaAuthentication authenticated = AuthenticationExtractor.getAuthentication();
    if (authenticated instanceof UserAuthentication userAuthentication) {
      GammaUser oldMe = userAuthentication.gammaUser();
      GammaUser newMe =
          oldMe
              .with()
              .nick(new Nick(updateMe.nick))
              .firstName(new FirstName(updateMe.firstName))
              .lastName(new LastName(updateMe.lastName))
              .language(Language.valueOf(updateMe.language))
              .build();

      this.userRepository.save(newMe);
    }
  }

  @Transactional
  public void updatePassword(UpdatePassword updatePassword)
      throws NewPasswordNotConfirmedException, PasswordIncorrectException {
    GammaAuthentication authenticated = AuthenticationExtractor.getAuthentication();
    if (authenticated instanceof UserAuthentication userAuthentication) {
      if (!updatePassword.newPassword.equals(updatePassword.confirmNewPassword)) {
        throw new NewPasswordNotConfirmedException();
      }

      GammaUser me = userAuthentication.gammaUser();
      this.userRepository.lockSignInMethods(me.id());
      if (!this.recentSignIn.isRecent()) {
        throw new AccessGuard.AccessDeniedException();
      }
      if (this.userRepository.hasPassword(me.id())
          && this.userRepository.checkPassword(
              me.id(), new UnencryptedPassword(updatePassword.oldPassword))) {
        this.userRepository.setPassword(
            me.id(), new UnencryptedPassword(updatePassword.newPassword));
        this.userRepository.revokeSessions(me.id());
        this.events.publishEvent(
            new SignInChanged(
                me.id(), "Your password was changed and your previous sessions were revoked."));
      } else {
        throw new PasswordIncorrectException();
      }
    }
  }

  @Transactional
  public void deleteMe(String password, boolean clientDataDeleted) {
    GammaAuthentication authenticated = AuthenticationExtractor.getAuthentication();
    if (authenticated instanceof UserAuthentication userAuthentication) {
      GammaUser me = userAuthentication.gammaUser();
      if (isMe(me, password)) {
        this.userRepository.lockForUpdate(me.id());
        if (!this.clientRepository.getAllUserClients(me.id()).isEmpty() && !clientDataDeleted) {
          throw new ClientDataAcknowledgementRequiredException();
        }
        try {
          this.dataDeletionRecorder.recordAccountDeletion(me);
          this.userRepository.delete(me.id());
        } catch (UserRepository.UserNotFoundException e) {
          throw new IllegalStateException();
        }
      } else {
        throw new IllegalArgumentException();
      }
    }
  }

  private boolean isMe(GammaUser me, String password) {
    if (!this.userRepository.hasPassword(me.id())) {
      return this.recentSignIn.isRecent();
    }

    UnencryptedPassword unencryptedPassword;
    try {
      unencryptedPassword = new UnencryptedPassword(password);
    } catch (IllegalArgumentException e) {
      return false;
    }
    return this.userRepository.checkPassword(me.id(), unencryptedPassword);
  }

  public boolean hasPassword() {
    this.accessGuard.require(isSignedIn());

    if (AuthenticationExtractor.getAuthentication()
        instanceof UserAuthentication userAuthentication) {
      return this.userRepository.hasPassword(userAuthentication.gammaUser().id());
    }
    return false;
  }

  @Transactional
  public void setAvatar(Image image) throws ImageService.ImageCouldNotBeSavedException {
    GammaAuthentication authenticated = AuthenticationExtractor.getAuthentication();
    if (authenticated instanceof UserAuthentication userAuthentication) {
      GammaUser user = userAuthentication.gammaUser();
      LOGGER.info("Image has been attempted to be uploaded by the user " + user.id().value());
      ImageUri imageUri = this.imageService.saveImage(image);
      this.userRepository.save(user.withExtended(user.extended().withAvatarUri(imageUri)));
      LOGGER.info("Image was successfully uploaded with the id: " + imageUri.value());
    } else {
      throw new ImageService.ImageCouldNotBeSavedException(
          "Could not find the authenticated user to upload the image");
    }
  }

  @Transactional
  public void deleteAvatar() throws ImageService.ImageCouldNotBeRemovedException {
    GammaAuthentication authenticated = AuthenticationExtractor.getAuthentication();
    if (authenticated instanceof UserAuthentication userAuthentication) {
      GammaUser user = userAuthentication.gammaUser();
      this.userRepository.save(user.withExtended(user.extended().withAvatarUri(null)));
      // Physical removal is queued transactionally by the avatar-row trigger.
    }
  }

  public static class ClientDataAcknowledgementRequiredException extends RuntimeException {}

  public record UserApprovedClientDTO(
      UUID clientUid,
      String name,
      String svDescription,
      String enDescription,
      List<String> scopes) {
    public UserApprovedClientDTO(Client client) {
      this(
          client.clientUid().value(),
          client.prettyName().value(),
          client.description().sv().value(),
          client.description().en().value(),
          client.scopes().stream().map(Enum::name).toList());
    }
  }

  public record AccountDeletionOverviewDTO(
      UUID userId,
      String cid,
      String firstName,
      List<String> officialClients,
      boolean hasClearedDeletionRecords,
      List<UnofficialClientOwnerDTO> unofficialClientOwners,
      List<String> ownedClients) {}

  private record OwnerContact(GammaUser owner, Email email, List<String> clients) {}

  public record UnofficialClientOwnerDTO(
      String name, String firstName, String email, List<String> clients) {}

  public record MyMembership(
      PostFacade.PostDTO post, GroupFacade.GroupDTO group, String unofficialPostName) {
    public MyMembership(UserMembership userMembership) {
      this(
          new PostFacade.PostDTO(userMembership.post()),
          new GroupFacade.GroupDTO(userMembership.group()),
          userMembership.unofficialPostName().value());
    }
  }

  public record MeDTO(
      String nick,
      String firstName,
      String lastName,
      String cid,
      String email,
      UUID id,
      int acceptanceYear,
      List<MyMembership> groups,
      String language,
      boolean isAdmin,
      boolean recoveryEmailUnverified) {
    public MeDTO(
        GammaUser user,
        List<MyMembership> groups,
        boolean isAdmin,
        boolean recoveryEmailUnverified) {
      this(
          user.nick().value(),
          user.firstName().value(),
          user.lastName().value(),
          user.cid().value(),
          user.extended().email().value(),
          user.id().value(),
          user.acceptanceYear().value(),
          groups,
          user.language().name(),
          isAdmin,
          recoveryEmailUnverified);
    }
  }

  public record UpdateMe(String nick, String firstName, String lastName, String language) {}

  public record UpdatePassword(String oldPassword, String newPassword, String confirmNewPassword) {}

  public static final class NewPasswordNotConfirmedException extends Exception {}

  public static final class PasswordIncorrectException extends Exception {}
}
