package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.MeFacade;
import it.chalmers.gamma.app.domain.Client;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/users/approval")
public class UserApprovalController {

    private final MeFacade meFacade;

    public UserApprovalController(MeFacade meFacade) {
        this.meFacade = meFacade;
    }

    @GetMapping()
    public List<Client> getApprovedClientsByUser() {
        return this.meFacade.getSignedInUserApprovals();
    }
}