package it.chalmers.gamma.factories;

import it.chalmers.gamma.service.ITUserApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MockITUserApprovalFactory {

    @Autowired
    private ITUserApprovalService approvalService;

    public void approve(String cid, String clientId) {
        this.approvalService.saveApproval(cid, clientId);
    }

}
