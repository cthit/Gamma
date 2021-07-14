package it.chalmers.gamma.app.user;

import it.chalmers.gamma.adapter.secondary.jpa.user.UserApprovalEntity;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserApprovalEntityPK;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserApprovalJpaRepository;
import it.chalmers.gamma.app.domain.Client;
import it.chalmers.gamma.app.domain.ClientId;
import it.chalmers.gamma.app.domain.User;
import it.chalmers.gamma.app.domain.UserApproval;
import it.chalmers.gamma.app.client.ClientService;
import it.chalmers.gamma.util.UserUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service()
public class UserApprovalService {

    private final UserApprovalJpaRepository repository;
    private final ClientService clientService;

    public UserApprovalService(UserApprovalJpaRepository repository,
                               ClientService clientService) {
        this.repository = repository;
        this.clientService = clientService;
    }

    public void create(UserApproval userApproval) {
        this.repository.save(new UserApprovalEntity(userApproval));
    }

    public void delete(UserApprovalEntityPK id) throws UserApprovalNotFoundException {
        this.repository.deleteById(id);
    }


    public UserApproval get(UserApprovalEntityPK userApprovalPK) throws UserService.UserNotFoundException {
        return this.repository.findById(userApprovalPK)
                .orElseThrow(UserService.UserNotFoundException::new)
                .toDomain();
    }

    public List<UserApproval> getApprovalsByClientId(ClientId clientId) {
        return this.repository.findAllById_ClientId(clientId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<Client> getSignedInUserApprovals() {
        Map<ClientId, Client> clientMap = clientService.getAll()
                .stream()
                .collect(Collectors.toMap(Client::clientId, Function.identity()));

        User user = UserUtils.getUserDetails().getUser();
        return this.repository.findAllById_UserId(user.id())
                .stream()
                .map(userApprovalEntity -> clientMap.get(userApprovalEntity.toDomain().clientId()))
                .collect(Collectors.toList());
    }

    protected UserApproval toDTO(UserApprovalEntity userApproval) {
        return new UserApproval(
                userApproval.id().value().userId(),
                userApproval.id().value().clientId()
        );
    }


    public static class UserApprovalNotFoundException extends Exception { }

}
