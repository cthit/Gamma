package it.chalmers.gamma.app.user;

import static it.chalmers.gamma.app.authentication.AccessGuard.isSignedIn;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.client.domain.Client;
import it.chalmers.gamma.app.client.domain.ClientRepository;
import it.chalmers.gamma.app.client.domain.ClientUid;
import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.group.GroupFacade;
import it.chalmers.gamma.app.group.domain.GroupRepository;
import it.chalmers.gamma.app.image.domain.Image;
import it.chalmers.gamma.app.image.domain.ImageService;
import it.chalmers.gamma.app.image.domain.ImageUri;
import it.chalmers.gamma.app.post.PostFacade;
import it.chalmers.gamma.app.user.domain.*;
import it.chalmers.gamma.security.authentication.AuthenticationExtractor;
import it.chalmers.gamma.security.authentication.GammaAuthentication;
import it.chalmers.gamma.security.authentication.UserAuthentication;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MeFacade extends Facade {

  private static final Logger LOGGER = LoggerFactory.getLogger(MeFacade.class);

  private final UserRepository userRepository;
  private final ClientRepository clientRepository;
  private final GroupRepository groupRepository;
  private final ImageService imageService;

  public MeFacade(
      AccessGuard accessGuard,
      UserRepository userRepository,
      ClientRepository clientRepository,
      GroupRepository groupRepository,
      ImageService imageService) {
    super(accessGuard);
    this.userRepository = userRepository;
    this.clientRepository = clientRepository;
    this.groupRepository = groupRepository;
    this.imageService = imageService;
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

  public void deleteUserApproval(UUID clientUid) {
    this.accessGuard.require(isSignedIn());

    if (AuthenticationExtractor.getAuthentication()
        instanceof UserAuthentication userAuthentication) {
      this.clientRepository.deleteUserApproval(
          new ClientUid(clientUid), userAuthentication.gammaUser().id());
    }
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

    return new MeDTO(user, groups, isAdmin);
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
              .extended(oldMe.extended().with().email(new Email(updateMe.email)).build())
              .build();

      this.userRepository.save(newMe);
    }
  }

  public void updatePassword(UpdatePassword updatePassword) {
    GammaAuthentication authenticated = AuthenticationExtractor.getAuthentication();
    if (authenticated instanceof UserAuthentication userAuthentication) {
      GammaUser me = userAuthentication.gammaUser();
      if (this.userRepository.checkPassword(
          me.id(), new UnencryptedPassword(updatePassword.oldPassword))) {
        this.userRepository.setPassword(
            me.id(), new UnencryptedPassword(updatePassword.newPassword));
      }
    }
  }

  public void deleteMe(String password) {
    GammaAuthentication authenticated = AuthenticationExtractor.getAuthentication();
    if (authenticated instanceof UserAuthentication userAuthentication) {
      GammaUser me = userAuthentication.gammaUser();
      if (this.userRepository.checkPassword(me.id(), new UnencryptedPassword(password))) {
        try {
          this.userRepository.delete(me.id());
        } catch (UserRepository.UserNotFoundException e) {
          e.printStackTrace();
        }
      }
    }
  }

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

  public void deleteAvatar() throws ImageService.ImageCouldNotBeRemovedException {
    GammaAuthentication authenticated = AuthenticationExtractor.getAuthentication();
    if (authenticated instanceof UserAuthentication userAuthentication) {
      GammaUser user = userAuthentication.gammaUser();
      this.userRepository.save(user.withExtended(user.extended().withAvatarUri(null)));
      this.imageService.removeImage(user.extended().avatarUri());
    }
  }

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
      boolean isAdmin) {
    public MeDTO(GammaUser user, List<MyMembership> groups, boolean isAdmin) {
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
          isAdmin);
    }
  }

  public record UpdateMe(
      String nick, String firstName, String lastName, String email, String language) {}

  public record UpdatePassword(String oldPassword, String newPassword) {}
}
