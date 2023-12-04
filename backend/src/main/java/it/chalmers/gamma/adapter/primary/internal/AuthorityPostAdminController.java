package it.chalmers.gamma.adapter.primary.internal;

import it.chalmers.gamma.app.client.domain.ClientAuthorityFacade;
import it.chalmers.gamma.util.response.AlreadyExistsResponse;
import it.chalmers.gamma.util.response.NotFoundResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/admin/authority/post")
public final class AuthorityPostAdminController {

    private final ClientAuthorityFacade clientAuthorityFacade;

    public AuthorityPostAdminController(ClientAuthorityFacade clientAuthorityFacade) {
        this.clientAuthorityFacade = clientAuthorityFacade;
    }

    @PostMapping
    public AuthorityPostCreatedResponse addAuthority(@RequestBody CreateAuthorityPostRequest request) {
        try {
            this.clientAuthorityFacade.addSuperGroupPostToClientAuthority(
                    request.clientUid,
                    request.authorityName,
                    request.superGroupId,
                    request.postId
            );
        } catch (ClientAuthorityFacade.ClientAuthorityNotFoundException e) {
            throw new AuthorityNotFoundResponse();
        } catch (ClientAuthorityFacade.SuperGroupNotFoundException | ClientAuthorityFacade.PostNotFoundException e) {
            throw new AuthorityPostNotFoundResponse();
        }
        return new AuthorityPostCreatedResponse();
    }

    @DeleteMapping
    public AuthorityPostRemovedResponse removeAuthority(
            @RequestParam("clientUid") UUID clientUid,
            @RequestParam("superGroupId") UUID superGroupId,
            @RequestParam("postId") UUID postId,
            @RequestParam("authorityName") String authorityName) {
        try {
            this.clientAuthorityFacade.removeSuperGroupPostFromClientAuthority(clientUid, authorityName, superGroupId, postId);
        } catch (ClientAuthorityFacade.ClientAuthorityNotFoundException e) {
            throw new AuthorityPostNotFoundResponse();
        }
        return new AuthorityPostRemovedResponse();
    }

    private record CreateAuthorityPostRequest(UUID clientUid, UUID postId, UUID superGroupId,
                                              String authorityName) {
    }

    private static class AuthorityNotFoundResponse extends NotFoundResponse {
    }

    private static class AuthorityPostRemovedResponse extends SuccessResponse {
    }

    private static class AuthorityPostCreatedResponse extends SuccessResponse {
    }

    private static class AuthorityPostNotFoundResponse extends NotFoundResponse {
    }

    private static class AuthorityPostAlreadyExistsResponse extends AlreadyExistsResponse {
    }

}

