package it.chalmers.gamma.internal.user.approval.controller;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.EntityName;
import it.chalmers.gamma.internal.client.service.ClientDTO;
import it.chalmers.gamma.internal.text.service.TextDTO;
import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.internal.user.approval.service.UserApprovalFinder;
import it.chalmers.gamma.internal.client.service.ClientFinder;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import it.chalmers.gamma.domain.UserId;
import it.chalmers.gamma.internal.user.service.UserFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/approval")
public class UserApprovalController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserApprovalController.class);

    private final ClientFinder clientFinder;
    private final UserApprovalFinder userApprovalFinder;
    private final UserFinder userFinder;

    public UserApprovalController(ClientFinder clientFinder,
                                  UserApprovalFinder userApprovalFinder,
                                  UserFinder userFinder) {
        this.clientFinder = clientFinder;
        this.userApprovalFinder = userApprovalFinder;
        this.userFinder = userFinder;
    }

    //TODO: Implement Delete

    public record ClientUserAccess(EntityName name, TextDTO description) implements DTO { }

    @GetMapping()
    public List<ClientUserAccess> getApprovedClientsByUser(Principal principal) {
        String cid = principal.getName();
        try {
            UserId userId = this.userFinder.get(new Cid(cid)).id();

            return this.userApprovalFinder.getApprovalsByUser(userId)
                    .stream()
                    .map(userApproval ->
                            {
                                try {
                                    ClientDTO client = clientFinder.get(userApproval.clientId());
                                    return new ClientUserAccess(
                                            client.name(),
                                            client.description()
                                    );
                                } catch (EntityNotFoundException e) {
                                    LOGGER.error("Client from user approvals not found", e);
                                    return null;
                                }
                            }
                    )
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (EntityNotFoundException e) {
            //TODO fix
            return null;
        }
    }
}
