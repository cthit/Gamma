package it.chalmers.gamma.approval.controller;

import it.chalmers.gamma.approval.service.UserApprovalService;
import it.chalmers.gamma.user.dto.UserRestrictedDTO;
import it.chalmers.gamma.approval.response.GetUserApprovalsResponse;
import it.chalmers.gamma.client.controller.response.ClientDoesNotExistException;
import it.chalmers.gamma.client.service.ClientService;

import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/users/approval")
public class UserApprovalAdminController {

    private final UserApprovalService userApprovalService;
    private final ClientService clientService;

    public UserApprovalAdminController(UserApprovalService userApprovalService, ClientService clientService) {
        this.userApprovalService = userApprovalService;
        this.clientService = clientService;
    }

    @GetMapping("/{clientId}")
    public GetUserApprovalsResponse getApprovalsByClientId(@PathVariable("clientId") String clientId) {
        if (!this.clientService.clientExists(clientId)) {
            throw new ClientDoesNotExistException();
        }

        return new GetUserApprovalsResponse(
                this.userApprovalService.getApprovalsByClientId(clientId)
                        .stream()
                        .map(itUserApprovalDTO -> new UserRestrictedDTO(itUserApprovalDTO.getUser()))
                        .collect(Collectors.toList())
        );
    }

}
