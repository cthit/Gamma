package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.domain.User;
import it.chalmers.gamma.app.domain.ClientId;

import java.util.List;

import it.chalmers.gamma.app.user.UserApprovalService;
import it.chalmers.gamma.app.user.UserService;
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
    public List<User> getApprovalsByClientId(@PathVariable("clientId") ClientId clientId) {
        return this.userApprovalService.getApprovalsByClientId(clientId);
    }
}
