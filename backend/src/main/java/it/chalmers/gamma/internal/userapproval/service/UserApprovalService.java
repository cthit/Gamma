package it.chalmers.gamma.internal.userapproval.service;

import it.chalmers.gamma.domain.Client;
import it.chalmers.gamma.domain.ClientId;
import it.chalmers.gamma.domain.User;
import it.chalmers.gamma.domain.UserApproval;
import it.chalmers.gamma.internal.client.service.ClientService;
import it.chalmers.gamma.internal.user.service.UserService;
import it.chalmers.gamma.util.UserUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service()
public class UserApprovalService {

    private final UserApprovalRepository repository;
    private final ClientService clientService;

    public UserApprovalService(UserApprovalRepository repository,
                               ClientService clientService) {
        this.repository = repository;
        this.clientService = clientService;
    }

    public void create(UserApproval userApproval) {
        this.repository.save(new UserApprovalEntity(userApproval));
    }

    public void delete(UserApprovalPK id) throws UserApprovalNotFoundException {
        this.repository.deleteById(id);
    }


    public UserApproval get(UserApprovalPK userApprovalPK) throws UserService.UserNotFoundException {
        return this.repository.findById(userApprovalPK)
                .orElseThrow(UserService.UserNotFoundException::new)
                .toDTO();
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
                .map(userApprovalEntity -> clientMap.get(userApprovalEntity.toDTO().clientId()))
                .collect(Collectors.toList());
    }

    protected UserApproval toDTO(UserApprovalEntity userApproval) {
        return new UserApproval(
                userApproval.id().get().userId(),
                userApproval.id().get().clientId()
        );
    }


    public static class UserApprovalNotFoundException extends Exception { }

}
