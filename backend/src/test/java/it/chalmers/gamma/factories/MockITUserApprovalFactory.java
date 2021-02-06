package it.chalmers.gamma.factories;

import it.chalmers.gamma.approval.service.UserApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MockITUserApprovalFactory {

    @Autowired
    private UserApprovalService approvalService;

    public void approve(String cid, String clientId) {
        this.approvalService.saveApproval(cid, clientId);
    }

}
