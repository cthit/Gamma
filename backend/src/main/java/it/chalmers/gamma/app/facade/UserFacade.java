package it.chalmers.gamma.app.facade;

import it.chalmers.gamma.app.AccessGuard;
import it.chalmers.gamma.app.port.service.PasswordService;
import it.chalmers.gamma.app.port.repository.UserRepository;
import it.chalmers.gamma.app.port.authentication.ApiAuthenticated;
import it.chalmers.gamma.app.port.authentication.AuthenticatedService;
import it.chalmers.gamma.app.port.repository.GroupRepository;
import it.chalmers.gamma.app.domain.client.Client;
import it.chalmers.gamma.app.domain.common.Email;
import it.chalmers.gamma.app.domain.user.Cid;
import it.chalmers.gamma.app.domain.user.FirstName;
import it.chalmers.gamma.app.domain.user.Language;
import it.chalmers.gamma.app.domain.user.LastName;
import it.chalmers.gamma.app.domain.user.Nick;
import it.chalmers.gamma.app.domain.user.UnencryptedPassword;
import it.chalmers.gamma.app.domain.user.User;
import it.chalmers.gamma.app.domain.user.UserId;
import it.chalmers.gamma.app.domain.user.UserMembership;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class UserFacade extends Facade {

    private final UserRepository userRepository;
    private final AuthenticatedService authenticatedService;
    private final PasswordService passwordService;
    private final GroupRepository groupRepository;

    public UserFacade(AccessGuard accessGuard,
                      UserRepository userRepository,
                      AuthenticatedService authenticatedService,
                      PasswordService passwordService,
                      GroupRepository groupRepository) {
        super(accessGuard);
        this.userRepository = userRepository;
        this.authenticatedService = authenticatedService;
        this.passwordService = passwordService;
        this.groupRepository = groupRepository;
    }

    public record UserDTO(String cid,
                          String nick,
                          String firstName,
                          String lastName,
                          UUID id,
                          int acceptanceYear,
                          String imageUri) {
        public UserDTO(User user) {
            this(user.cid().value(),
                    user.nick().value(),
                    user.firstName().value(),
                    user.lastName().value(),
                    user.id().value(),
                    user.acceptanceYear().value(),
                    user.imageUri().value());
        }
    }

    public record UserGroupDTO(GroupFacade.GroupDTO group, PostFacade.PostDTO post) {
        public UserGroupDTO(UserMembership userMembership) {
            this(
                    new GroupFacade.GroupDTO(userMembership.group()),
                    new PostFacade.PostDTO(userMembership.post())
            );
        }
    }
    public record UserWithGroupsDTO(UserDTO user, List<UserGroupDTO> groups) { }

    public Optional<UserWithGroupsDTO> get(String cid) {
        this.accessGuard.requireSignedIn();
        Optional<UserDTO> maybeUser = this.userRepository.get(new Cid(cid)).map(UserDTO::new);
        if (maybeUser.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(
                new UserWithGroupsDTO(
                        maybeUser.get(),
                        getUserGroups(new UserId(maybeUser.get().id))
                )
        );
    }

    public Optional<UserWithGroupsDTO> get(UUID id) {
        this.accessGuard.requireSignedIn();
        UserId userId = new UserId(id);

        Optional<UserDTO> maybeUser = this.userRepository.get(userId).map(UserDTO::new);
        if (maybeUser.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(
                new UserWithGroupsDTO(
                        maybeUser.get(),
                        getUserGroups(userId)
                )
        );
    }

    private List<UserGroupDTO> getUserGroups(UserId userId) {
        return this.groupRepository.getGroupsByUser(userId)
                .stream()
                .map(UserGroupDTO::new)
                .toList();
    }

    public List<UserDTO> getAll() {
        this.accessGuard.requireSignedIn();
        return this.userRepository.getAll().stream().map(UserDTO::new).toList();
    }

    public List<UserDTO> getAllByClientAccepting() {
        this.accessGuard.requireIsClientApi();
        if (authenticatedService.getAuthenticated() instanceof ApiAuthenticated apiAuthenticated) {
            Client client = apiAuthenticated.getClient().orElseThrow();
            return client.approvedUsers()
                    .stream()
                    .map(UserDTO::new)
                    .toList();
        }
        return Collections.emptyList();
    }

    public void setUserPassword(UUID id, String newPassword) {
        this.accessGuard.requireIsAdmin();
        User oldUser = this.userRepository.get(new UserId(id)).orElseThrow();
        oldUser.withPassword(
                this.passwordService.encrypt(new UnencryptedPassword(newPassword))
        );
    }

    public void deleteUser(UUID id) {
        this.accessGuard.requireIsAdmin();
        try {
            this.userRepository.delete(new UserId(id));
        } catch (UserRepository.UserNotFoundException e) {
            e.printStackTrace();
        }
    }

    public record UserExtendedDTO(String cid,
                                  String nick,
                                  String firstName,
                                  String lastName,
                                  UUID id,
                                  int acceptanceYear,
                                  String email,
                                  boolean gdprTrained,
                                  boolean locked) {
        public UserExtendedDTO(User user) {
            this(user.cid().value(),
                    user.nick().value(),
                    user.firstName().value(),
                    user.lastName().value(),
                    user.id().value(),
                    user.acceptanceYear().value(),
                    user.email().value(),
                    user.gdprTrained(),
                    user.locked());
        }
    }

    public record UserExtendedWithGroupsDTO(UserExtendedDTO user, List<UserGroupDTO> groups) { }

    public Optional<UserExtendedWithGroupsDTO> getAsAdmin(UUID id) {
        this.accessGuard.requireIsAdmin();
        UserId userId = new UserId(id);

        Optional<UserExtendedDTO> maybeUser = this.userRepository.get(userId).map(UserExtendedDTO::new);
        if (maybeUser.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(
                new UserExtendedWithGroupsDTO(
                        maybeUser.get(),
                        getUserGroups(userId)
                )
        );
    }

    public record UpdateUser(UUID id,
                             String nick,
                             String firstName,
                             String lastName,
                             String email,
                             String language) { }

    public void updateUser(UpdateUser updateUser) {
        accessGuard.requireIsAdmin();
        User oldUser = this.userRepository.get(new UserId(updateUser.id)).orElseThrow();
        this.userRepository.save(
                oldUser.with()
                        .nick(new Nick(updateUser.nick))
                        .firstName(new FirstName(updateUser.firstName))
                        .lastName(new LastName(updateUser.lastName))
                        .email(new Email(updateUser.email))
                        .language(Language.valueOf(updateUser.language))
                        .build()
        );
    }

    public void updateGdprTrainedStatus(UUID userId, boolean gdprTrained) {
        accessGuard.requireIsAdmin();
        User oldUser = this.userRepository.get(new UserId(userId)).orElseThrow();
        this.userRepository.save(
                oldUser.withGdprTrained(gdprTrained)
        );
    }

}