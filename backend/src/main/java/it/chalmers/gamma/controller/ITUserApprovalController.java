package it.chalmers.gamma.controller;

import it.chalmers.gamma.domain.dto.access.ITClientUserAccessDTO;
import it.chalmers.gamma.response.client.ApprovedITClientsResponse;
import it.chalmers.gamma.service.ITUserApprovalService;

import java.security.Principal;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/approval")
public class ITUserApprovalController {

    private final ITUserApprovalService itUserApprovalService;

    public ITUserApprovalController(ITUserApprovalService itUserApprovalService) {
        this.itUserApprovalService = itUserApprovalService;
    }

    @GetMapping()
    public ApprovedITClientsResponse getApprovedClientsByUser(Principal principal) {
        String cid = principal.getName();

        return new ApprovedITClientsResponse(
                this.itUserApprovalService.getApprovalsByCid(cid)
                .stream()
                .map(itUserApprovalDTO -> new ITClientUserAccessDTO(itUserApprovalDTO.getClient()))
                .collect(Collectors.toList())
        );
    }

}
