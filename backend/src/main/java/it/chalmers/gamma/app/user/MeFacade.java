package it.chalmers.gamma.app.user;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.user.domain.GammaUser;
import it.chalmers.gamma.security.principal.GammaPrincipal;
import it.chalmers.gamma.security.principal.GammaSecurityContextUtils;
import it.chalmers.gamma.security.principal.UserPrincipal;
import it.chalmers.gamma.security.principal.LockedUserPrincipal;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelRepository;
import it.chalmers.gamma.app.client.domain.Client;
import it.chalmers.gamma.app.client.domain.ClientRepository;
import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.group.domain.GroupRepository;
import it.chalmers.gamma.app.user.domain.FirstName;
import it.chalmers.gamma.app.user.domain.Language;
import it.chalmers.gamma.app.user.domain.LastName;
import it.chalmers.gamma.app.user.domain.Nick;
import it.chalmers.gamma.app.user.domain.UnencryptedPassword;
import it.chalmers.gamma.app.user.domain.UserAuthority;
import it.chalmers.gamma.app.user.domain.UserMembership;
import it.chalmers.gamma.app.user.domain.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static it.chalmers.gamma.app.authentication.AccessGuard.isSignedIn;
import static it.chalmers.gamma.security.principal.GammaSecurityContextUtils.getPrincipal;

@Service
public class MeFacade extends Facade {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final AuthorityLevelRepository authorityLevelRepository;
    private final GroupRepository groupRepository;

    public MeFacade(AccessGuard accessGuard,
                    UserRepository userRepository,
                    ClientRepository clientRepository,
                    AuthorityLevelRepository authorityLevelRepository,
                    GroupRepository groupRepository) {
        super(accessGuard);
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.authorityLevelRepository = authorityLevelRepository;
        this.groupRepository = groupRepository;
    }

    public record UserApprovedClientDTO(String prettyName,
                                        String svDescription,
                                        String enDescription) {
        public UserApprovedClientDTO(Client client) {
            this(client.prettyName().value(),
                    client.description().sv().value(),
                    client.description().en().value());
        }
    }

    public List<UserApprovedClientDTO> getSignedInUserApprovals() {
        this.accessGuard.require(isSignedIn());

        if (getPrincipal() instanceof UserPrincipal userPrincipal) {
            GammaUser user = userPrincipal.get();
            return this.clientRepository.getClientsByUserApproved(user.id())
                    .stream()
                    .map(UserApprovedClientDTO::new)
                    .toList();
        } else {
            return null;
        }
    }

    public record MyAuthority(String authority, String type) { }

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
                        List<MyAuthority> authorities,
                        String language) {
        public MeDTO(GammaUser user, List<UserMembership> groups, List<UserAuthority> authorities) {
            this(user.nick().value(),
                    user.firstName().value(),
                    user.lastName().value(),
                    user.cid().value(),
                    user.extended().email().value(),
                    user.id().value(),
                    user.acceptanceYear().value(),
                    user.extended().gdprTrained(),
                    user.extended().acceptedUserAgreement(),
                    groups,
                    authorities.stream().map(a -> new MyAuthority(a.authorityLevelName().value(), a.authorityType().name())).toList(),
                    user.language().name()
            );
        }
    }


    public MeDTO getMe() {
        GammaPrincipal authenticated = GammaSecurityContextUtils.getPrincipal();
        GammaUser user = null;
        if (authenticated instanceof UserPrincipal userPrincipal) {
            user = userPrincipal.get();
        }

        if (user == null) {
            throw new IllegalCallerException("Can only be called by signed in sessions");
        }

        List<UserMembership> groups = this.groupRepository.getAllByUser(user.id());
        List<UserAuthority> authorities = this.authorityLevelRepository.getByUser(user.id());

        return new MeDTO(user, groups, authorities);
    }

    public record UpdateMe(String nick,
                           String firstName,
                           String lastName,
                           String email,
                           String language) { }


    public void updateMe(UpdateMe updateMe) {
        GammaPrincipal authenticated = getPrincipal();
        if (authenticated instanceof UserPrincipal userPrincipal) {
            GammaUser oldMe = userPrincipal.get();
            GammaUser newMe = oldMe.with()
                    .nick(new Nick(updateMe.nick))
                    .firstName(new FirstName(updateMe.firstName))
                    .lastName(new LastName(updateMe.lastName))
                    .language(Language.valueOf(updateMe.language))
                    .extended(oldMe.extended().with()
                            .email(new Email(updateMe.email))
                            .build()
                    )
                    .build();

            this.userRepository.save(newMe);
        }
    }

    public record UpdatePassword(String oldPassword, String newPassword) { }

    public void updatePassword(UpdatePassword updatePassword) {
        GammaPrincipal authenticated = getPrincipal();
        if (authenticated instanceof UserPrincipal userPrincipal) {
            GammaUser me = userPrincipal.get();
            if (this.userRepository.checkPassword(me.id(), new UnencryptedPassword(updatePassword.oldPassword))) {
                this.userRepository.setPassword(me.id(), new UnencryptedPassword(updatePassword.newPassword));
            }
        }
    }

    public void deleteMe(String password) {
        GammaPrincipal authenticated = getPrincipal();
        if (authenticated instanceof UserPrincipal userPrincipal) {
            GammaUser me = userPrincipal.get();
            if (this.userRepository.checkPassword(me.id(), new UnencryptedPassword(password))) {
                try {
                    this.userRepository.delete(me.id());
                } catch (UserRepository.UserNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //TODO: Should only locked user be able to accept user agreement?
    //TODO: I guess you cannot accept a user agreement if you already have accepted it.
    public void acceptUserAgreement() {
        GammaPrincipal authenticated = getPrincipal();
        if (authenticated instanceof LockedUserPrincipal lockedUserPrincipal) {
            try {
                this.userRepository.acceptUserAgreement(lockedUserPrincipal.get().id());
            } catch (UserRepository.UserNotFoundException e) {
                throw new IllegalStateException();
            }
        }
    }
}
