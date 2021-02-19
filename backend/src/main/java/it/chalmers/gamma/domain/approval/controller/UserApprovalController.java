package it.chalmers.gamma.domain.approval.controller;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.approval.service.UserApprovalFinder;
import it.chalmers.gamma.domain.client.exception.ClientNotFoundException;
import it.chalmers.gamma.domain.client.service.ClientFinder;
import it.chalmers.gamma.domain.client.data.ClientUserAccessDTO;
import it.chalmers.gamma.domain.client.controller.response.ApprovedClientsResponse;
import it.chalmers.gamma.domain.approval.service.UserApprovalService;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import it.chalmers.gamma.domain.user.UserId;
import it.chalmers.gamma.domain.user.controller.response.UserNotFoundResponse;
import it.chalmers.gamma.domain.user.exception.UserNotFoundException;
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

    private final UserApprovalService userApprovalService;
    private final ClientFinder clientFinder;
    private final UserApprovalFinder userApprovalFinder;
    private final UserFinder userFinder;

    public UserApprovalController(UserApprovalService userApprovalService,
                                  ClientFinder clientFinder,
                                  UserApprovalFinder userApprovalFinder,
                                  UserFinder userFinder) {
        this.userApprovalService = userApprovalService;
        this.clientFinder = clientFinder;
        this.userApprovalFinder = userApprovalFinder;
        this.userFinder = userFinder;
    }

    @GetMapping()
    public ApprovedClientsResponse getApprovedClientsByUser(Principal principal) {
        String cid = principal.getName();
        try {
            UserId userId = this.userFinder.getUser(new Cid(cid)).getId();

            List<ClientUserAccessDTO> userAccesses = this.userApprovalFinder.getApprovalsByUser(userId)
                    .stream()
                    .map(userApproval ->
                            {
                                try {
                                    return new ClientUserAccessDTO(
                                            clientFinder.getClient(userApproval.getClientId())
                                    );
                                } catch (ClientNotFoundException e) {
                                    LOGGER.error("Client from user approvals not found", e);
                                    return null;
                                }
                            }
                    )
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            return new ApprovedClientsResponse(userAccesses);
        } catch (UserNotFoundException e) {
            LOGGER.error("Can't find signed in user", e);
            throw new UserNotFoundResponse();
        }
    }
}
