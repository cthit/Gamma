package it.chalmers.gamma.adapter.primary.internal;

import it.chalmers.gamma.app.client.domain.ClientAuthorityFacade;
import it.chalmers.gamma.util.response.AlreadyExistsResponse;
import it.chalmers.gamma.util.response.NotFoundResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/admin/authority/supergroup")
public final class AuthoritySuperGroupAdminController {

    private final ClientAuthorityFacade clientAuthorityFacade;

    public AuthoritySuperGroupAdminController(ClientAuthorityFacade clientAuthorityFacade) {
        this.clientAuthorityFacade = clientAuthorityFacade;
    }

    @PostMapping
    public AuthoritySuperGroupCreatedResponse addAuthority(@RequestBody CreateAuthoritySuperGroupRequest request) {
        try {
            this.clientAuthorityFacade.addSuperGroupToClientAuthority(
                    request.clientUid,
                    request.authorityName,
                    request.superGroupId
            );
        } catch (ClientAuthorityFacade.ClientAuthorityNotFoundException e) {
            throw new ClientAuthorityNotFoundResponse();
        } catch (ClientAuthorityFacade.SuperGroupNotFoundException e) {
            e.printStackTrace();
        }
        return new AuthoritySuperGroupCreatedResponse();
    }

    @DeleteMapping
    public AuthoritySuperGroupRemovedResponse removeAuthority(
            @RequestParam("clientUid") UUID clientUid,
            @RequestParam("superGroupId") UUID superGroupId,
            @RequestParam("authorityName") String authorityName) {
        try {
            this.clientAuthorityFacade.removeSuperGroupFromClientAuthority(
                    clientUid,
                    authorityName,
                    superGroupId
            );
        } catch (ClientAuthorityFacade.ClientAuthorityNotFoundException e) {
            throw new ClientAuthorityNotFoundResponse();
        }
        return new AuthoritySuperGroupRemovedResponse();
    }

    private record CreateAuthoritySuperGroupRequest(UUID clientUid, UUID superGroupId, String authorityName) {
    }

    private static class AuthoritySuperGroupRemovedResponse extends SuccessResponse {
    }

    private static class AuthoritySuperGroupCreatedResponse extends SuccessResponse {
    }

    private static class ClientAuthorityNotFoundResponse extends NotFoundResponse {
    }

    private static class AuthoritySuperGroupNotFoundResponse extends NotFoundResponse {
    }

    private static class AuthoritySuperGroupAlreadyExistsResponse extends AlreadyExistsResponse {
    }


}
