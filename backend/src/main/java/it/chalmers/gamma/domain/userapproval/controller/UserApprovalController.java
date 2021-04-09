package it.chalmers.gamma.domain.userapproval.controller;

import it.chalmers.gamma.util.domain.Cid;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.domain.userapproval.service.UserApprovalFinder;
import it.chalmers.gamma.domain.client.service.ClientFinder;
import it.chalmers.gamma.domain.client.service.ClientUserAccessDTO;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import it.chalmers.gamma.domain.user.service.UserId;
import it.chalmers.gamma.domain.user.service.UserFinder;
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

    @GetMapping()
    public ApprovedClientsResponse getApprovedClientsByUser(Principal principal) {
        String cid = principal.getName();
        try {
            UserId userId = this.userFinder.get(new Cid(cid)).getId();

            List<ClientUserAccessDTO> userAccesses = this.userApprovalFinder.getApprovalsByUser(userId)
                    .stream()
                    .map(userApproval ->
                            {
                                try {
                                    return new ClientUserAccessDTO(
                                            clientFinder.get(userApproval.getClientId())
                                    );
                                } catch (EntityNotFoundException e) {
                                    LOGGER.error("Client from user approvals not found", e);
                                    return null;
                                }
                            }
                    )
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            return new ApprovedClientsResponse(userAccesses);
        } catch (EntityNotFoundException e) {
            //TODO fix
            return null;
        }
    }
}
