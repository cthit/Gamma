package it.chalmers.gamma.internal.userapproval.controller;

import it.chalmers.gamma.domain.UserApproval;
import it.chalmers.gamma.domain.ClientId;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import it.chalmers.gamma.internal.userapproval.service.UserApprovalService;
import it.chalmers.gamma.domain.UserRestricted;
import it.chalmers.gamma.internal.user.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/admin/users/approval")
public class UserApprovalAdminController {

    private final UserService userService;
    private final UserApprovalService userApprovalService;

    public UserApprovalAdminController(UserService userService,
                                       UserApprovalService userApprovalService) {
        this.userService = userService;
        this.userApprovalService = userApprovalService;
    }

    @GetMapping("/{clientId}")
    public List<UserRestricted> getApprovalsByClientId(@PathVariable("clientId") ClientId clientId) {
        return this.userApprovalService.getApprovalsByClientId(clientId)
                    .stream()
                    .map(this::toUserRestricted)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
    }

    private UserRestricted toUserRestricted(UserApproval userApproval) {
        try {
            return new UserRestricted(this.userService.get(userApproval.userId()));
        } catch (UserService.UserNotFoundException ignored) {
            return null;
        }
    }

}
