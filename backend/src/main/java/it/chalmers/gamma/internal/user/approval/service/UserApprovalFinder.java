package it.chalmers.gamma.internal.user.approval.service;

import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.util.domain.abstraction.GetEntity;
import it.chalmers.gamma.domain.ClientId;
import it.chalmers.gamma.domain.UserId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserApprovalFinder implements GetEntity<UserApprovalPK, UserApprovalDTO> {

    private final UserApprovalRepository userApprovalRepository;

    public UserApprovalFinder(UserApprovalRepository userApprovalRepository) {
        this.userApprovalRepository = userApprovalRepository;
    }

    public UserApprovalDTO get(UserApprovalPK userApprovalPK) throws EntityNotFoundException {
        return this.userApprovalRepository.findById(userApprovalPK)
                .orElseThrow(EntityNotFoundException::new)
                .toDTO();
    }

    public List<UserApprovalDTO> getApprovalsByClientId(ClientId clientId) {
        return this.userApprovalRepository.findAllById_ClientId(clientId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<UserApprovalDTO> getApprovalsByUser(UserId userId) {
        return this.userApprovalRepository.findAllById_UserId(userId)
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

}

