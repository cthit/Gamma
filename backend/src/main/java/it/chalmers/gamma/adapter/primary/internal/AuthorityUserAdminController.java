package it.chalmers.gamma.adapter.primary.internal;

import it.chalmers.gamma.app.client.domain.ClientAuthorityFacade;
import it.chalmers.gamma.util.response.AlreadyExistsResponse;
import it.chalmers.gamma.util.response.NotFoundResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/admin/authority/user")
public final class AuthorityUserAdminController {

    private final ClientAuthorityFacade clientAuthorityFacade;

    public AuthorityUserAdminController(ClientAuthorityFacade clientAuthorityFacade) {
        this.clientAuthorityFacade = clientAuthorityFacade;
    }

    @PostMapping
    public ClientAuthorityUserCreatedResponse addAuthority(@RequestBody CreateClientAuthorityUserRequest request) {
        try {
            this.clientAuthorityFacade.addUserToClientAuthority(
                    request.clientUid,
                    request.authorityName,
                    request.userId
            );
        } catch (ClientAuthorityFacade.ClientAuthorityNotFoundException e) {
            throw new ClientAuthorityNotFoundResponse();
        } catch (ClientAuthorityFacade.UserNotFoundException e) {
            throw new ClientAuthorityUserNotFoundResponse();
        }
        return new ClientAuthorityUserCreatedResponse();
    }

    @DeleteMapping
    public ClientAuthorityUserRemovedResponse removeAuthority(
            @RequestParam("clientUid") UUID clientUid,
            @RequestParam("userId") UUID userId,
            @RequestParam("authorityName") String authorityName) {
        try {
            this.clientAuthorityFacade.removeUserFromClientAuthority(
                    clientUid,
                    authorityName,
                    userId
            );
        } catch (ClientAuthorityFacade.ClientAuthorityNotFoundException e) {
            throw new ClientAuthorityUserNotFoundResponse();
        }
        return new ClientAuthorityUserRemovedResponse();
    }

    private record CreateClientAuthorityUserRequest(UUID clientUid, UUID userId, String authorityName) {
    }

    private static class ClientAuthorityUserRemovedResponse extends SuccessResponse {
    }

    private static class ClientAuthorityUserCreatedResponse extends SuccessResponse {
    }

    private static class ClientAuthorityUserNotFoundResponse extends NotFoundResponse {
    }

    private static class ClientAuthorityNotFoundResponse extends NotFoundResponse {
    }

    private static class AuthorityUserAlreadyExistsResponse extends AlreadyExistsResponse {
    }

}
