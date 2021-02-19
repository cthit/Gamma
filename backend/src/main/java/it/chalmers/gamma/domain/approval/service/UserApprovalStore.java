package it.chalmers.gamma.domain.approval.service;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.approval.data.UserApproval;
import it.chalmers.gamma.domain.approval.exception.UserApprovalNotFoundException;
import it.chalmers.gamma.domain.client.exception.ClientNotFoundException;
import it.chalmers.gamma.domain.user.UserId;
import it.chalmers.gamma.domain.user.exception.UserNotFoundException;
import it.chalmers.gamma.domain.user.service.UserFinder;
import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.hibernate.bytecode.BytecodeLogger.LOGGER;

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
                this.userApprovalService.saveApproval(approval);
            } catch (UserNotFoundException e) {
                LOGGER.error("User not found in addApprovals", e);
            } catch (ClientNotFoundException e) {
                LOGGER.error("Client not found in addApprovals", e);
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
            UserId userId = this.userFinder.getUser(new Cid(cid)).getId();

            UserApproval userApproval = this.userApprovalFinder.getApproval(userId, clientId);
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
        } catch (UserNotFoundException e) {
            LOGGER.error("User not found", e);
            return new ArrayList<>();
        } catch (UserApprovalNotFoundException e) {
            LOGGER.error("User approval not found", e);
            return new ArrayList<>();
        }
    }
}
