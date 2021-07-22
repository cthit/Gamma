package it.chalmers.gamma.app;

import it.chalmers.gamma.app.client.ClientRepository;
import it.chalmers.gamma.app.domain.Client;
import it.chalmers.gamma.app.domain.User;
import org.springframework.stereotype.Service;

import java.util.List;

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


}
