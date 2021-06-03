package it.chalmers.gamma.internal.user.approval.service;

import it.chalmers.gamma.domain.ClientId;
import it.chalmers.gamma.domain.UserId;
import it.chalmers.gamma.internal.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service()
public class UserApprovalService {

    private final UserApprovalRepository repository;

    public UserApprovalService(UserApprovalRepository repository) {
        this.repository = repository;
    }

    public void create(UserApprovalDTO userApproval) {
        this.repository.save(new UserApprovalEntity(userApproval));
    }

    public void delete(UserApprovalPK id) throws UserApprovalNotFoundException {
        this.repository.deleteById(id);
    }


    public UserApprovalDTO get(UserApprovalPK userApprovalPK) throws UserService.UserNotFoundException {
        return this.repository.findById(userApprovalPK)
                .orElseThrow(UserService.UserNotFoundException::new)
                .toDTO();
    }

    public List<UserApprovalDTO> getApprovalsByClientId(ClientId clientId) {
        return this.repository.findAllById_ClientId(clientId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<UserApprovalDTO> getApprovalsByUser(UserId userId) {
        return this.repository.findAllById_UserId(userId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    protected UserApprovalDTO toDTO(UserApprovalEntity userApproval) {
        return new UserApprovalDTO(
                userApproval.id().get().userId(),
                userApproval.id().get().clientId()
        );
    }


    public static class UserApprovalNotFoundException extends Exception { }

}
