package it.chalmers.gamma.internal.user.approval.service;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.internal.user.service.UserDTO;
import it.chalmers.gamma.domain.ClientId;
import it.chalmers.gamma.domain.UserId;
import it.chalmers.gamma.internal.user.service.UserService;
import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserApprovalStore implements ApprovalStore {

    private final UserApprovalService userApprovalService;
    private final UserService userService;

    public UserApprovalStore(UserApprovalService userApprovalService,
                             UserService userService) {
        this.userApprovalService = userApprovalService;
        this.userService = userService;
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
                UserDTO user = this.userService.get(new Cid(cid));

                ClientId clientId = new ClientId(approval.getClientId());

                this.userApprovalService.create(new UserApprovalDTO(user.id(), clientId));
            } catch (UserService.UserNotFoundException e) {
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
            UserId userId = this.userService.get(new Cid(cid)).id();

            UserApprovalDTO userApproval = this.userApprovalService.get(new UserApprovalPK(userId, new ClientId(clientId)));
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
        } catch (UserService.UserNotFoundException e) {
            return new ArrayList<>();
        }
    }
}
