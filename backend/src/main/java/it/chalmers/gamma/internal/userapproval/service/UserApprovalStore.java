package it.chalmers.gamma.internal.userapproval.service;

import it.chalmers.gamma.util.domain.Cid;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.internal.user.service.UserDTO;
import it.chalmers.gamma.internal.client.service.ClientId;
import it.chalmers.gamma.internal.user.service.UserId;
import it.chalmers.gamma.internal.user.service.UserFinder;
import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserApprovalStore implements ApprovalStore {

    private final UserApprovalService userApprovalService;
    private final UserApprovalFinder userApprovalFinder;
    private final UserFinder userFinder;

    public UserApprovalStore(UserApprovalService userApprovalService,
                             UserApprovalFinder userApprovalFinder,
                             UserFinder userFinder) {
        this.userApprovalService = userApprovalService;
        this.userApprovalFinder = userApprovalFinder;
        this.userFinder = userFinder;
    }

    @Override
    public boolean addApprovals(Collection<Approval> approvals) {
        Optional<Approval> accessApproval = approvals
                .stream()
                .filter(approval -> approval.getScope().equals("access"))
                .findFirst();

        accessApproval.ifPresent(approval -> {
            try {
                String cid = approval.getUserId();
                UserDTO user = this.userFinder.get(new Cid(cid));

                ClientId clientId = new ClientId(approval.getClientId());

                this.userApprovalService.create(new UserApprovalDTO(user.id(), clientId));
            } catch (EntityNotFoundException e) {
                e.printStackTrace();
            }
        });

        return true;
    }

    @Override
    public boolean revokeApprovals(Collection<Approval> approvals) {
        return false;
    }

    @Override
    public Collection<Approval> getApprovals(String cid, String clientId) {
        try {
            UserId userId = this.userFinder.get(new Cid(cid)).id();

            UserApprovalDTO userApproval = this.userApprovalFinder.get(new UserApprovalPK(userId, new ClientId(clientId)));
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
        } catch (EntityNotFoundException e) {
            return new ArrayList<>();
        }
    }
}
