package it.chalmers.gamma.internal.user.approval.controller;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.EntityName;
import it.chalmers.gamma.internal.client.service.ClientDTO;
import it.chalmers.gamma.internal.client.service.ClientService;
import it.chalmers.gamma.internal.text.service.TextDTO;
import it.chalmers.gamma.internal.user.approval.service.UserApprovalService;
import it.chalmers.gamma.internal.user.service.UserService;
import it.chalmers.gamma.util.domain.abstraction.DTO;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import it.chalmers.gamma.domain.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/users/approval")
public class UserApprovalController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserApprovalController.class);

    private final ClientService clientService;
    private final UserService userService;
    private final UserApprovalService userApprovalService;

    public UserApprovalController(ClientService clientService,
                                  UserService userService,
                                  UserApprovalService userApprovalService) {
        this.clientService = clientService;
        this.userService = userService;
        this.userApprovalService = userApprovalService;
    }

    //TODO: Implement Delete

    public record ClientUserAccess(EntityName name, TextDTO description) implements DTO { }

    @GetMapping()
    public List<ClientUserAccess> getApprovedClientsByUser(Principal principal) {
        String cid = principal.getName();
        try {
            UserId userId = this.userService.get(new Cid(cid)).id();

            return this.userApprovalService.getApprovalsByUser(userId)
                    .stream()
                    .map(userApproval ->
                            {
                                try {
                                    ClientDTO client = clientService.get(userApproval.clientId());
                                    return new ClientUserAccess(
                                            client.name(),
                                            client.description()
                                    );
                                } catch (ClientService.ClientNotFoundException e) {
                                    LOGGER.error("Client from user approvals not found", e);
                                    return null;
                                }
                            }
                    )
                    .collect(Collectors.toList());
        } catch (UserService.UserNotFoundException e) {
            //TODO fix
            return null;
        }
    }
}
