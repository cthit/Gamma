package it.chalmers.gamma.factories;

import it.chalmers.gamma.service.ITUserApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class MockITUserApprovalFactory {

    @Autowired
    private ITUserApprovalService approvalService;

    public void approve(String cid, String clientId) {
        approvalService.saveApproval(cid, clientId);
    }

}
