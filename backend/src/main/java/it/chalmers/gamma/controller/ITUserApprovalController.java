package it.chalmers.gamma.controller;

import it.chalmers.gamma.client.ITClientFinder;
import it.chalmers.gamma.client.ITClientUserAccessDTO;
import it.chalmers.gamma.client.response.ApprovedITClientsResponse;
import it.chalmers.gamma.approval.ITUserApprovalService;

import java.security.Principal;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/approval")
public class ITUserApprovalController {

    private final ITUserApprovalService itUserApprovalService;
    private final ITClientFinder clientFinder;

    public ITUserApprovalController(ITUserApprovalService itUserApprovalService, ITClientFinder clientFinder) {
        this.itUserApprovalService = itUserApprovalService;
        this.clientFinder = clientFinder;
    }

    @GetMapping()
    public ApprovedITClientsResponse getApprovedClientsByUser(Principal principal) {
        String cid = principal.getName();

        return new ApprovedITClientsResponse(
                this.itUserApprovalService.getApprovalsByCid(cid)
                .stream()
                .map(userApproval ->
                        new ITClientUserAccessDTO(
                                clientFinder.getClient(userApproval.getClientId()).orElseThrow()
                        )
                )
                .collect(Collectors.toList())
        );
    }

}
