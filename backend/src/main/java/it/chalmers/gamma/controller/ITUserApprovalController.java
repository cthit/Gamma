package it.chalmers.gamma.controller;

import it.chalmers.gamma.client.service.ClientFinder;
import it.chalmers.gamma.client.dto.ClientUserAccessDTO;
import it.chalmers.gamma.client.controller.response.ApprovedClientsResponse;
import it.chalmers.gamma.approval.service.UserApprovalService;

import java.security.Principal;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/approval")
public class ITUserApprovalController {

    private final UserApprovalService userApprovalService;
    private final ClientFinder clientFinder;

    public ITUserApprovalController(UserApprovalService userApprovalService, ClientFinder clientFinder) {
        this.userApprovalService = userApprovalService;
        this.clientFinder = clientFinder;
    }

    @GetMapping()
    public ApprovedClientsResponse getApprovedClientsByUser(Principal principal) {
        String cid = principal.getName();

        return new ApprovedClientsResponse(
                this.userApprovalService.getApprovalsByCid(cid)
                .stream()
                .map(userApproval ->
                        new ClientUserAccessDTO(
                                clientFinder.getClient(userApproval.getClientId()).orElseThrow()
                        )
                )
                .collect(Collectors.toList())
        );
    }

}
