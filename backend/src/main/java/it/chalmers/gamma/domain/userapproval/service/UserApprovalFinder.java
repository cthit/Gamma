package it.chalmers.gamma.domain.userapproval.service;

import it.chalmers.gamma.domain.EntityNotFoundException;
import it.chalmers.gamma.domain.GetEntity;
import it.chalmers.gamma.domain.userapproval.data.db.UserApproval;
import it.chalmers.gamma.domain.userapproval.data.dto.UserApprovalDTO;
import it.chalmers.gamma.domain.userapproval.data.db.UserApprovalPK;
import it.chalmers.gamma.domain.userapproval.data.db.UserApprovalRepository;
import it.chalmers.gamma.domain.client.domain.ClientId;
import it.chalmers.gamma.domain.user.UserId;
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

    protected UserApprovalDTO toDTO(UserApproval userApproval) {
        return new UserApprovalDTO(
                userApproval.getId().getUserId(),
                userApproval.getId().getClientId()
        );
    }

}

