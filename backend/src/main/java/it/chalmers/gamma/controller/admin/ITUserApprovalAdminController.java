package it.chalmers.gamma.controller.admin;

import it.chalmers.gamma.domain.dto.user.ITUserRestrictedDTO;
import it.chalmers.gamma.response.approval.GetAllITUserApprovalResponse;
import it.chalmers.gamma.response.client.ITClientDoesNotExistException;
import it.chalmers.gamma.service.ITClientService;
import it.chalmers.gamma.service.ITUserApprovalService;

import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/users/approval")
public class ITUserApprovalAdminController {

    private final ITUserApprovalService itUserApprovalService;
    private final ITClientService itClientService;

    public ITUserApprovalAdminController(ITUserApprovalService itUserApprovalService, ITClientService itClientService) {
        this.itUserApprovalService = itUserApprovalService;
        this.itClientService = itClientService;
    }

    @GetMapping("/{clientId}")
    public GetAllITUserApprovalResponse getApprovalsByClientId(@PathVariable("clientId") String clientId) {
        if (!this.itClientService.clientExists(clientId)) {
            throw new ITClientDoesNotExistException();
        }

        return new GetAllITUserApprovalResponse(
                this.itUserApprovalService.getApprovalsByClientId(clientId)
                        .stream()
                        .map(itUserApprovalDTO -> new ITUserRestrictedDTO(itUserApprovalDTO.getUser()))
                        .collect(Collectors.toList())
        );
    }

}
