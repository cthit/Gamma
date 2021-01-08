package it.chalmers.gamma.approval;

import it.chalmers.gamma.domain.user.ITUserRestrictedDTO;
import it.chalmers.gamma.approval.response.GetAllITUserApprovalResponse;
import it.chalmers.gamma.client.response.ITClientDoesNotExistException;
import it.chalmers.gamma.client.ITClientService;

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
