package it.chalmers.gamma.adapter.secondary.oauth;

import it.chalmers.gamma.app.client.ClientUserApprovalUseCase;
import it.chalmers.gamma.domain.user.Cid;
import it.chalmers.gamma.domain.client.ClientId;
import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserApprovalStore implements ApprovalStore {

    private final ClientUserApprovalUseCase userApprovalRepository;

    public UserApprovalStore(ClientUserApprovalUseCase userApprovalRepository) {
        this.userApprovalRepository = userApprovalRepository;
    }

    @Override
    public boolean addApprovals(Collection<Approval> approvals) {
        Optional<Approval> accessApproval = approvals
                .stream()
                .filter(approval -> approval.getScope().equals("access"))
                .findFirst();

        accessApproval.ifPresent(approval -> {
            Cid cid = Cid.valueOf(approval.getUserId());
            ClientId clientId = ClientId.valueOf(approval.getClientId());

            userApprovalRepository.approveUserForClient(cid, clientId);
        });

        return true;
    }

    @Override
    public boolean revokeApprovals(Collection<Approval> approvals) {
        return false;
    }

    @Override
    public Collection<Approval> getApprovals(String cidString, String clientIdString) {
        Cid cid = Cid.valueOf(cidString);
        ClientId clientId = ClientId.valueOf(clientIdString);

        return this.userApprovalRepository.userHaveApprovedClient(cid, clientId)
                ? Collections.singleton(
                    new Approval(
                            cid.value(),
                            clientId.value(),
                            "access",
                            Integer.MAX_VALUE,
                            Approval.ApprovalStatus.APPROVED
                    )
                )
                : Collections.emptyList();
    }

}
