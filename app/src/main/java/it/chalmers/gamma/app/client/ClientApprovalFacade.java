package it.chalmers.gamma.app.client;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.client.domain.ClientUid;
import it.chalmers.gamma.app.client.domain.approval.ClientApprovalsRepository;
import it.chalmers.gamma.app.user.UserFacade;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static it.chalmers.gamma.app.authentication.AccessGuard.isAdmin;

@Component
public class ClientApprovalFacade extends Facade {

    private final ClientApprovalsRepository clientApprovalsRepository;

    public ClientApprovalFacade(AccessGuard accessGuard, ClientApprovalsRepository clientApprovalsRepository) {
        super(accessGuard);
        this.clientApprovalsRepository = clientApprovalsRepository;
    }

    public List<UserFacade.UserDTO> getApprovalsForClient(UUID clientUid) {
        accessGuard.require(isAdmin());

        return this.clientApprovalsRepository.getAllByClientUid(new ClientUid(clientUid))
                .stream().map(UserFacade.UserDTO::new).toList();
    }

}
