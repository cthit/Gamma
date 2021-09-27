package it.chalmers.gamma.app.facade;

import it.chalmers.gamma.app.AccessGuard;
import it.chalmers.gamma.app.port.authentication.Authenticated;
import it.chalmers.gamma.app.port.authentication.AuthenticatedService;
import it.chalmers.gamma.app.port.authentication.UserAuthenticated;
import it.chalmers.gamma.app.port.repository.AuthorityLevelRepository;
import it.chalmers.gamma.app.port.repository.ClientRepository;
import it.chalmers.gamma.app.port.repository.GroupRepository;
import it.chalmers.gamma.app.port.service.PasswordService;
import it.chalmers.gamma.app.port.repository.UserRepository;
import it.chalmers.gamma.app.domain.client.Client;
import it.chalmers.gamma.app.domain.common.Email;
import it.chalmers.gamma.app.domain.user.FirstName;
import it.chalmers.gamma.app.domain.user.Language;
import it.chalmers.gamma.app.domain.user.LastName;
import it.chalmers.gamma.app.domain.user.Nick;
import it.chalmers.gamma.app.domain.user.UnencryptedPassword;
import it.chalmers.gamma.app.domain.user.UserAuthority;
import it.chalmers.gamma.app.domain.user.UserMembership;
import it.chalmers.gamma.app.domain.user.User;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class MeFacade extends Facade {

    private final UserRepository userRepository;
    private final AuthenticatedService authenticatedService;
    private final ClientRepository clientRepository;
    private final AuthorityLevelRepository authorityLevelRepository;
    private final GroupRepository groupRepository;
    private final PasswordService passwordService;

    public MeFacade(AccessGuard accessGuard,
                    UserRepository userRepository,
                    AuthenticatedService authenticatedService,
                    ClientRepository clientRepository,
                    AuthorityLevelRepository authorityLevelRepository,
                    GroupRepository groupRepository,
                    PasswordService passwordService) {
        super(accessGuard);
        this.userRepository = userRepository;
        this.authenticatedService = authenticatedService;
        this.clientRepository = clientRepository;
        this.authorityLevelRepository = authorityLevelRepository;
        this.groupRepository = groupRepository;
        this.passwordService = passwordService;
    }

    public List<Client> getSignedInUserApprovals() {
        accessGuard.requireSignedIn();
        if (authenticatedService.getAuthenticated() instanceof UserAuthenticated userAuthenticated) {
            User user = userAuthenticated.get();
            return this.clientRepository.getClientsByUserApproved(user.id());
        } else {
            return null;
        }
    }

    public record MyAuthority(String authority, String type) {

    }

    public record MeDTO(String nick,
                        String firstName,
                        String lastName,
                        String cid,
                        String email,
                        UUID id,
                        int acceptanceYear,
                        boolean gdprTrained,
                        boolean userAgreement,
                        List<UserMembership> groups,
                        List<MyAuthority> authorities) {
        public MeDTO(User user, List<UserMembership> groups, List<UserAuthority> authorities) {
            //TODO: Find a good way to calculate userAgreement
            this(user.nick().value(),
                    user.firstName().value(),
                    user.lastName().value(),
                    user.cid().value(),
                    user.email().value(),
                    user.id().value(),
                    user.acceptanceYear().value(),
                    user.gdprTrained(),
                    true,
                    groups,
                    authorities.stream().map(a -> new MyAuthority(a.authorityLevelName().value(), a.authorityType().name())).toList());
        }
    }


    public MeDTO getMe() {
        Authenticated authenticated = this.authenticatedService.getAuthenticated();
        if (authenticated instanceof UserAuthenticated userAuthenticated) {
            User user = userAuthenticated.get();
            List<UserMembership> groups = this.groupRepository.getGroupsByUser(user.id());
            List<UserAuthority> authorities = this.authorityLevelRepository.getByUser(user.id());
            return new MeDTO(user, groups, authorities);
        } else {
            throw new IllegalCallerException("Can only be called by signed in sessions");
        }
    }

    public record UpdateMe(String nick,
                           String firstName,
                           String lastName,
                           String email,
                           String language) { }


    public void updateMe(UpdateMe updateMe) {
        Authenticated authenticated = this.authenticatedService.getAuthenticated();
        if (authenticated instanceof UserAuthenticated userAuthenticated) {
            User oldMe = userAuthenticated.get();
            User newMe = oldMe.with()
                    .nick(new Nick(updateMe.nick))
                    .firstName(new FirstName(updateMe.firstName))
                    .lastName(new LastName(updateMe.lastName))
                    .email(new Email(updateMe.email))
                    .language(Language.valueOf(updateMe.language))
                    .build();

            this.userRepository.save(newMe);
        }
    }

    public record UpdatePassword(String oldPassword, String newPassword) { }

    public void updatePassword(UpdatePassword updatePassword) {
        Authenticated authenticated = this.authenticatedService.getAuthenticated();
        if (authenticated instanceof UserAuthenticated userAuthenticated) {
            User oldMe = userAuthenticated.get();
            User newMe = oldMe
                    .withPassword(this.passwordService.encrypt(new UnencryptedPassword(updatePassword.newPassword)));

            this.userRepository.save(newMe);
        }
    }

    public void deleteMe(String password) {
        Authenticated authenticated = this.authenticatedService.getAuthenticated();
        if (authenticated instanceof UserAuthenticated userAuthenticated) {
            User me = userAuthenticated.get();
            if (this.passwordService.matches(new UnencryptedPassword(password), me.password())) {
                try {
                    this.userRepository.delete(me.id());
                } catch (UserRepository.UserNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void acceptUserAgreement() {
        Authenticated authenticated = this.authenticatedService.getAuthenticated();
        if (authenticated instanceof UserAuthenticated userAuthenticated) {
            User oldMe = userAuthenticated.get();
            User newMe = oldMe.withLastAcceptedUserAgreement(Instant.now());

            this.userRepository.save(newMe);
        }
    }
}
