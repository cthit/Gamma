package it.chalmers.gamma.approval;

import it.chalmers.gamma.client.ITClientDTO;
import it.chalmers.gamma.client.ITClientFinder;
import it.chalmers.gamma.client.ITClientService;
import it.chalmers.gamma.domain.Cid;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import it.chalmers.gamma.user.ITUserDTO;
import it.chalmers.gamma.user.ITUserFinder;
import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.stereotype.Service;

@Service("itUserApprovalService")
public class ITUserApprovalService implements ApprovalStore {

    private final ITUserApprovalRepository itUserApprovalRepository;
    private final ITUserFinder userFinder;
    private final ITClientFinder clientFinder;

    public ITUserApprovalService(ITUserApprovalRepository itUserApprovalRepository, ITUserFinder userFinder, ITClientFinder clientFinder) {
        this.itUserApprovalRepository = itUserApprovalRepository;
        this.userFinder = userFinder;
        this.clientFinder = clientFinder;
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

    public void saveApproval(String cid, String clientId) {
        ITUserDTO user = this.userFinder.getUser(new Cid(cid));
        ITClientDTO client = this.clientFinder.getClient(clientId).orElseThrow();

        ITUserApprovalPK itUserApprovalPK = new ITUserApprovalPK();
        itUserApprovalPK.setUser(user.getId());
        itUserApprovalPK.setClient(client.getId());

        ITUserApproval itUserApproval = new ITUserApproval();
        itUserApproval.setId(itUserApprovalPK);

        this.itUserApprovalRepository.save(itUserApproval);
    }

    private void saveApproval(Approval approval) {
        String cid = approval.getUserId();
        String clientId = approval.getClientId();

        saveApproval(cid, clientId);
    }

    public List<ITUserApprovalDTO> getApprovalsByClientId(String clientId) {
        return this.itUserApprovalRepository.findAllById_ItClient_ClientIdContaining(clientId)
                .stream()
                .map(ITUserApproval::toDTO)
                .collect(Collectors.toList());
    }

    public List<ITUserApprovalDTO> getApprovalsByCid(String cid) {
        return this.itUserApprovalRepository.findAllById_ItUser_CidContaining(cid)
                .stream()
                .map(ITUserApproval::toDTO)
                .collect(Collectors.toList());
    }

}
