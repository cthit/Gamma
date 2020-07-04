package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.ITUserApproval;
import it.chalmers.gamma.db.entity.pk.ITUserApprovalPK;
import it.chalmers.gamma.db.repository.ITUserApprovalRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.stereotype.Service;

@Service("itUserApprovalService")
public class ITUserApprovalService implements ApprovalStore {

    private final ITUserApprovalRepository itUserApprovalRepository;
    private final ITClientService itClientService;
    private final ITUserService itUserService;

    public ITUserApprovalService(
            ITUserApprovalRepository itUserApprovalRepository,
            ITClientService itClientService,
            ITUserService itUserService) {
        this.itUserApprovalRepository = itUserApprovalRepository;
        this.itClientService = itClientService;
        this.itUserService = itUserService;
    }

    @Override
    public boolean addApprovals(Collection<Approval> approvals) {
        Optional<Approval> accessApproval = approvals
                .stream()
                .filter(approval -> approval.getScope().equals("access"))
                .findFirst();

        accessApproval.ifPresent(this::saveApproval);

        return true;
    }

    @Override
    public boolean revokeApprovals(Collection<Approval> approvals) {
        //TODO
        return false;
    }

    @Override
    public Collection<Approval> getApprovals(String cid, String clientId) {
        ITUserApproval itUserApproval = this.itUserApprovalRepository
                .findById_ItUserCidContainingAndId_ItClient_ClientIdContaining(cid, clientId);
        return itUserApproval == null
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

    private void saveApproval(Approval approval) {
        String cid = approval.getUserId();
        String clientId = approval.getClientId();

        ITUserApprovalPK itUserApprovalPK = new ITUserApprovalPK();
        itUserApprovalPK.setItUser(this.itUserService.getITUser(this.itUserService.getITUser(cid)));
        itUserApprovalPK.setItClient(this.itClientService.getITClient(
                Objects.requireNonNull(this.itClientService.getITClientById(clientId).orElse(null)))
        );

        ITUserApproval itUserApproval = new ITUserApproval();
        itUserApproval.setId(itUserApprovalPK);

        this.itUserApprovalRepository.save(itUserApproval);
    }

}
