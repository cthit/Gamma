package it.chalmers.gamma.internal.userapproval.controller;

import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.internal.userapproval.service.UserApprovalFinder;
import it.chalmers.gamma.internal.client.service.ClientId;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import it.chalmers.gamma.internal.user.service.UserRestrictedDTO;
import it.chalmers.gamma.internal.user.service.UserFinder;
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
    public List<UserRestrictedDTO> getApprovalsByClientId(@PathVariable("clientId") ClientId clientId) {
        return this.userApprovalFinder.getApprovalsByClientId(clientId)
                    .stream()
                    .map(userApproval -> {
                        try {
                            return new UserRestrictedDTO(this.userFinder.get(userApproval.userId()));
                        } catch (EntityNotFoundException ignored) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
    }

}
