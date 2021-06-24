package it.chalmers.gamma.app.userapproval.controller;

import it.chalmers.gamma.app.domain.Client;
import it.chalmers.gamma.app.userapproval.service.UserApprovalService;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/users/approval")
public class UserApprovalController {

    private final UserApprovalService userApprovalService;

    public UserApprovalController(UserApprovalService userApprovalService) {
        this.userApprovalService = userApprovalService;
    }

    @GetMapping()
    public List<Client> getApprovedClientsByUser() {
        return this.userApprovalService.getSignedInUserApprovals();
    }
}