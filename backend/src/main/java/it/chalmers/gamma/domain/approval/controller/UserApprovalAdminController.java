package it.chalmers.gamma.domain.approval.controller;

import it.chalmers.gamma.domain.approval.service.UserApprovalFinder;
import it.chalmers.gamma.domain.approval.service.UserApprovalService;
import it.chalmers.gamma.domain.approval.response.GetUserApprovalsResponse;
import it.chalmers.gamma.domain.client.service.ClientService;

import java.util.Objects;
import java.util.stream.Collectors;

import it.chalmers.gamma.domain.user.data.UserRestrictedDTO;
import it.chalmers.gamma.domain.user.exception.UserNotFoundException;
import it.chalmers.gamma.domain.user.service.UserFinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/users/approval")
public class UserApprovalAdminController {

    private final UserFinder userFinder;
    private final UserApprovalService userApprovalService;
    private final ClientService clientService;
    private final UserApprovalFinder userApprovalFinder;

    public UserApprovalAdminController(UserFinder userFinder,
                                       UserApprovalService userApprovalService,
                                       ClientService clientService,
                                       UserApprovalFinder userApprovalFinder) {
        this.userFinder = userFinder;
        this.userApprovalService = userApprovalService;
        this.clientService = clientService;
        this.userApprovalFinder = userApprovalFinder;
    }

    @GetMapping("/{clientId}")
    public GetUserApprovalsResponse getApprovalsByClientId(@PathVariable("clientId") String clientId) {
        return new GetUserApprovalsResponse(
                this.userApprovalFinder.getApprovalsByClientId(clientId)
                        .stream()
                        .map(userApproval -> {
                            try {
                                return new UserRestrictedDTO(this.userFinder.getUser(userApproval.getUserId()));
                            } catch (UserNotFoundException ignored) {
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
        );
    }

}
