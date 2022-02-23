package it.chalmers.gamma.app.user;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.apikey.domain.ApiKeyType;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.authentication.ApiAuthenticated;
import it.chalmers.gamma.app.authentication.AuthenticatedService;
import it.chalmers.gamma.app.client.domain.Client;
import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.group.GroupFacade;
import it.chalmers.gamma.app.group.domain.GroupRepository;
import it.chalmers.gamma.app.post.PostFacade;
import it.chalmers.gamma.app.settings.domain.Settings;
import it.chalmers.gamma.app.settings.domain.SettingsRepository;
import it.chalmers.gamma.app.user.domain.FirstName;
import it.chalmers.gamma.app.user.domain.Language;
import it.chalmers.gamma.app.user.domain.LastName;
import it.chalmers.gamma.app.user.domain.Nick;
import it.chalmers.gamma.app.user.domain.UnencryptedPassword;
import it.chalmers.gamma.app.user.domain.User;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserMembership;
import it.chalmers.gamma.app.user.domain.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static it.chalmers.gamma.app.authentication.AccessGuard.isAdmin;
import static it.chalmers.gamma.app.authentication.AccessGuard.isApi;
import static it.chalmers.gamma.app.authentication.AccessGuard.isClientApi;
import static it.chalmers.gamma.app.authentication.AccessGuard.isSignedIn;
import static it.chalmers.gamma.app.authentication.AccessGuard.userHasAcceptedClient;

@Component
public class UserFacade extends Facade {

    private final UserRepository userRepository;
    private final AuthenticatedService authenticatedService;
    private final GroupRepository groupRepository;
    private final SettingsRepository settingsRepository;

    public UserFacade(AccessGuard accessGuard,
                      UserRepository userRepository,
                      AuthenticatedService authenticatedService,
                      GroupRepository groupRepository,
                      SettingsRepository settingsRepository) {
        super(accessGuard);
        this.userRepository = userRepository;
        this.authenticatedService = authenticatedService;
        this.groupRepository = groupRepository;
        this.settingsRepository = settingsRepository;
    }

    public record UserDTO(String cid,
                          String nick,
                          String firstName,
                          String lastName,
                          UUID id,
                          int acceptanceYear) {

        public UserDTO(User user) {
            this(user.cid().value(),
                    user.nick().value(),
                    user.firstName().value(),
                    user.lastName().value(),
                    user.id().value(),
                    user.acceptanceYear().value());
        }
    }

    public record UserGroupDTO(GroupFacade.GroupWithMembersDTO group, PostFacade.PostDTO post) {
        public UserGroupDTO(UserMembership userMembership) {
            this(
                    new GroupFacade.GroupWithMembersDTO(userMembership.group()),
                    new PostFacade.PostDTO(userMembership.post())
            );
        }
    }
    public record UserWithGroupsDTO(UserDTO user, List<UserGroupDTO> groups) { }

    public Optional<UserWithGroupsDTO> get(UUID id) {
        UserId userId = new UserId(id);
        accessGuard.requireEither(
                isSignedIn(),
                userHasAcceptedClient(userId),
                isApi(ApiKeyType.INFO)
        );

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
        return this.groupRepository.getAllByUser(userId)
                .stream()
                .map(UserGroupDTO::new)
                .toList();
    }

    public List<UserDTO> getAll() {
        accessGuard.require(isSignedIn());

        return this.userRepository.getAll().stream().map(UserDTO::new).toList();
    }

    public List<UserDTO> getAllByClientAccepting() {
        this.accessGuard.require(isClientApi());

        Settings settings = settingsRepository.getSettings();

        if (authenticatedService.getAuthenticated() instanceof ApiAuthenticated apiAuthenticated) {
            Client client = apiAuthenticated.getClient().orElseThrow();
            return client.approvedUsers()
                    .stream()
                    .filter(user -> user.extended().acceptedUserAgreement())
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

    public record UserExtendedDTO(String cid,
                                  String nick,
                                  String firstName,
                                  String lastName,
                                  UUID id,
                                  int version,
                                  int acceptanceYear,
                                  String email,
                                  boolean gdprTrained,
                                  boolean locked,
                                  boolean userAgreement,
                                  String language) {

        public UserExtendedDTO(User user) {
            this(user.cid().value(),
                    user.nick().value(),
                    user.firstName().value(),
                    user.lastName().value(),
                    user.id().value(),
                    user.extended().version(),
                    user.acceptanceYear().value(),
                    user.extended().email().value(),
                    user.extended().gdprTrained(),
                    user.extended().locked(),
                    user.extended().acceptedUserAgreement(),
                    user.language().name()
            );
        }
    }
    public record UserExtendedWithGroupsDTO(UserExtendedDTO user, List<UserGroupDTO> groups) { }

    public Optional<UserExtendedWithGroupsDTO> getAsAdmin(UUID id) {
        accessGuard.require(isAdmin());

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
        accessGuard.require(isAdmin());

        User oldUser = this.userRepository.get(new UserId(updateUser.id)).orElseThrow();
        this.userRepository.save(
                oldUser.with()
                        .nick(new Nick(updateUser.nick))
                        .firstName(new FirstName(updateUser.firstName))
                        .lastName(new LastName(updateUser.lastName))
                        .language(Language.valueOf(updateUser.language))
                        .extended(oldUser.extended().with()
                                .email(new Email(updateUser.email))
                                .build()
                        )
                        .build()
        );
    }

}