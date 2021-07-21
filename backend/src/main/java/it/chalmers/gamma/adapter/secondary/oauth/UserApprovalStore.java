package it.chalmers.gamma.adapter.secondary.oauth;

import it.chalmers.gamma.adapter.secondary.jpa.user.UserApprovalEntityPK;
import it.chalmers.gamma.app.domain.Cid;
import it.chalmers.gamma.app.domain.User;
import it.chalmers.gamma.app.domain.UserApproval;
import it.chalmers.gamma.app.domain.ClientId;
import it.chalmers.gamma.app.domain.UserId;
import it.chalmers.gamma.app.user.UserApprovalService;
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
                User user = this.userService.get(Cid.valueOf(cid));

                ClientId clientId = ClientId.valueOf(approval.getClientId());

                this.userApprovalService.create(new UserApproval(user.id(), clientId));
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
            UserId userId = this.userService.get(Cid.valueOf(cid)).id();

            UserApproval userApproval = this.userApprovalService.get(new UserApprovalEntityPK(userId, ClientId.valueOf(clientId)));
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
