package it.chalmers.gamma.approval.service;

import it.chalmers.gamma.approval.dto.UserApprovalDTO;
import it.chalmers.gamma.approval.data.UserApproval;
import it.chalmers.gamma.approval.data.UserApprovalPK;
import it.chalmers.gamma.approval.data.UserApprovalRepository;
import it.chalmers.gamma.client.dto.ClientDTO;
import it.chalmers.gamma.client.service.ClientFinder;
import it.chalmers.gamma.domain.Cid;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import it.chalmers.gamma.user.dto.UserDTO;
import it.chalmers.gamma.user.exception.UserNotFoundException;
import it.chalmers.gamma.user.service.UserFinder;
import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.stereotype.Service;

@Service()
public class UserApprovalService implements ApprovalStore {

    private final UserApprovalRepository userApprovalRepository;
    private final UserFinder userFinder;
    private final ClientFinder clientFinder;

    public UserApprovalService(UserApprovalRepository userApprovalRepository, UserFinder userFinder, ClientFinder clientFinder) {
        this.userApprovalRepository = userApprovalRepository;
        this.userFinder = userFinder;
        this.clientFinder = clientFinder;
    }

    @Override
    public boolean addApprovals(Collection<Approval> approvals) {
        Optional<Approval> accessApproval = approvals
                .stream()
                .filter(approval -> approval.getScope().equals("access"))
                .findFirst();

        accessApproval.ifPresent(approval -> {
            try {
                saveApproval(approval);
            } catch (UserNotFoundException e) {
                e.printStackTrace();
            }
        });

        return true;
    }

    @Override
    public boolean revokeApprovals(Collection<Approval> approvals) {
        //TODO
        return false;
    }

    @Override
    public Collection<Approval> getApprovals(String cid, String clientId) {
        UserApproval userApproval = this.userApprovalRepository
                .findById_ItUserCidContainingAndId_ItClient_ClientIdContaining(cid, clientId);
        return userApproval == null
                ? Collections.emptyList()
                : Collections.singleton(
                        new Approval(
                                cid,
                                clientId,
                                "access",
                                Integer.MAX_VALUE,
                                Approval.ApprovalStatus.APPROVED
                        )
        );
    }

    public void saveApproval(String cid, String clientId) throws UserNotFoundException {
        UserDTO user = this.userFinder.getUser(new Cid(cid));
        ClientDTO client = this.clientFinder.getClient(clientId);

        UserApprovalPK userApprovalPK = new UserApprovalPK();
        userApprovalPK.setUser(user.getId());
        userApprovalPK.setClient(client.getId());

        UserApproval userApproval = new UserApproval();
        userApproval.setId(userApprovalPK);

        this.userApprovalRepository.save(userApproval);
    }

    private void saveApproval(Approval approval) throws UserNotFoundException {
        String cid = approval.getUserId();
        String clientId = approval.getClientId();

        saveApproval(cid, clientId);
    }

    public List<UserApprovalDTO> getApprovalsByClientId(String clientId) {
        return this.userApprovalRepository.findAllById_ItClient_ClientIdContaining(clientId)
                .stream()
                .map(UserApproval::toDTO)
                .collect(Collectors.toList());
    }

    public List<UserApprovalDTO> getApprovalsByCid(String cid) {
        return this.userApprovalRepository.findAllById_ItUser_CidContaining(cid)
                .stream()
                .map(UserApproval::toDTO)
                .collect(Collectors.toList());
    }

}
