package it.chalmers.gamma.domain.userapproval.controller;

import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.domain.userapproval.service.UserApprovalFinder;
import it.chalmers.gamma.domain.userapproval.response.GetUserApprovalsResponse;
import it.chalmers.gamma.domain.client.domain.ClientId;

import java.util.Objects;
import java.util.stream.Collectors;

import it.chalmers.gamma.domain.user.data.dto.UserRestrictedDTO;
import it.chalmers.gamma.domain.user.service.UserFinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/users/approval")
public class UserApprovalAdminController {

    private final UserFinder userFinder;
    private final UserApprovalFinder userApprovalFinder;

    public UserApprovalAdminController(UserFinder userFinder,
                                       UserApprovalFinder userApprovalFinder) {
        this.userFinder = userFinder;
        this.userApprovalFinder = userApprovalFinder;
    }

    @GetMapping("/{clientId}")
    public GetUserApprovalsResponse getApprovalsByClientId(@PathVariable("clientId") ClientId clientId) {
        return new GetUserApprovalsResponse(
                this.userApprovalFinder.getApprovalsByClientId(clientId)
                        .stream()
                        .map(userApproval -> {
                            try {
                                return new UserRestrictedDTO(this.userFinder.get(userApproval.getUserId()));
                            } catch (EntityNotFoundException ignored) {
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
        );
    }

}
