package it.chalmers.gamma.app.user;

import it.chalmers.gamma.app.AccessGuard;
import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.authentication.Authenticated;
import it.chalmers.gamma.app.authentication.AuthenticatedService;
import it.chalmers.gamma.app.authentication.UserAuthenticated;
import it.chalmers.gamma.app.authoritylevel.AuthorityLevelRepository;
import it.chalmers.gamma.app.client.ClientRepository;
import it.chalmers.gamma.app.group.GroupRepository;
import it.chalmers.gamma.domain.client.Client;
import it.chalmers.gamma.domain.user.UserAuthority;
import it.chalmers.gamma.domain.user.UserMembership;
import it.chalmers.gamma.domain.user.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MeFacade extends Facade {

    private final AuthenticatedService authenticatedService;
    private final ClientRepository clientRepository;
    private final AuthorityLevelRepository authorityLevelRepository;
    private final GroupRepository groupRepository;

    public MeFacade(AccessGuard accessGuard,
                    AuthenticatedService authenticatedService,
                    ClientRepository clientRepository,
                    AuthorityLevelRepository authorityLevelRepository,
                    GroupRepository groupRepository) {
        super(accessGuard);
        this.authenticatedService = authenticatedService;
        this.clientRepository = clientRepository;
        this.authorityLevelRepository = authorityLevelRepository;
        this.groupRepository = groupRepository;
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
}
