package it.chalmers.gamma.domain.approval.service;

import it.chalmers.gamma.domain.approval.data.UserApproval;
import it.chalmers.gamma.domain.approval.data.UserApprovalDTO;
import it.chalmers.gamma.domain.approval.data.UserApprovalPK;
import it.chalmers.gamma.domain.approval.data.UserApprovalRepository;
import it.chalmers.gamma.domain.approval.exception.UserApprovalNotFoundException;
import it.chalmers.gamma.domain.client.ClientId;
import it.chalmers.gamma.domain.user.UserId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserApprovalFinder {

    private final UserApprovalRepository userApprovalRepository;

    public UserApprovalFinder(UserApprovalRepository userApprovalRepository) {
        this.userApprovalRepository = userApprovalRepository;
    }

    public UserApproval getApproval(UserId userId, ClientId clientId) throws UserApprovalNotFoundException {
        return this.userApprovalRepository.findById(new UserApprovalPK(userId, clientId))
                .orElseThrow(UserApprovalNotFoundException::new);
    }

    public List<UserApprovalDTO> getApprovalsByClientId(String clientId) {
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

