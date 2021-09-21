package it.chalmers.gamma.app.user;

import it.chalmers.gamma.app.AccessGuard;
import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.authentication.Authenticated;
import it.chalmers.gamma.app.authentication.AuthenticatedService;
import it.chalmers.gamma.app.authentication.UserAuthenticated;
import it.chalmers.gamma.app.client.ClientRepository;
import it.chalmers.gamma.domain.client.Client;
import it.chalmers.gamma.domain.user.User;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;
import java.util.UUID;

@Service
public class MeFacade extends Facade {

    private final AuthenticatedService authenticatedService;
    private final ClientRepository clientRepository;

    public MeFacade(AccessGuard accessGuard, AuthenticatedService authenticatedService, ClientRepository clientRepository) {
        super(accessGuard);
        this.authenticatedService = authenticatedService;
        this.clientRepository = clientRepository;
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
                        List<MyAuthority> authorities) {
        public MeDTO(User user) {
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
                    user.authorities()
                            .stream()
                            .map(userAuthority ->
                                    new MyAuthority(
                                            userAuthority.authorityLevelName().value(),
                                            userAuthority.authorityType().name()))
                            .toList());
        }
    }


    public MeDTO getMe() {
        Authenticated authenticated = this.authenticatedService.getAuthenticated();
        if (authenticated instanceof UserAuthenticated userAuthenticated) {
            return new MeDTO(userAuthenticated.get());
        } else {
            throw new IllegalCallerException("Can only be called by signed in sessions");
        }
    }
}
