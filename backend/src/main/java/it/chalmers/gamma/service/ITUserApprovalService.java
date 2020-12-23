package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.ITUserApproval;
import it.chalmers.gamma.db.entity.pk.ITUserApprovalPK;
import it.chalmers.gamma.repository.ITUserApprovalRepository;
import it.chalmers.gamma.domain.dto.user.ITUserApprovalDTO;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import it.chalmers.gamma.user.ITUserService;
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

    public void saveApproval(String cid, String clientId) {
        ITUserApprovalPK itUserApprovalPK = new ITUserApprovalPK();
        itUserApprovalPK.setItUser(this.itUserService.getITUser(this.itUserService.getITUser(cid)));
        itUserApprovalPK.setItClient(this.itClientService.getITClient(
                Objects.requireNonNull(this.itClientService.getITClientById(clientId).orElse(null)))
        );

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
