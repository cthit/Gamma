package it.chalmers.gamma.adapter.primary.internal;

import it.chalmers.gamma.app.authority.ClientAuthorityFacade;
import it.chalmers.gamma.app.authority.domain.ClientAuthorityRepository;
import it.chalmers.gamma.util.response.AlreadyExistsResponse;
import it.chalmers.gamma.util.response.NotFoundResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/internal/admin/client")
public final class ClientAuthorityAdminController {

    private final ClientAuthorityFacade clientAuthorityFacade;

    public ClientAuthorityAdminController(ClientAuthorityFacade clientAuthorityFacade) {
        this.clientAuthorityFacade = clientAuthorityFacade;
    }

    @PostMapping("/authority")
    public ClientAuthorityCreatedResponse addClientAuthority(@RequestBody CreateClientAuthorityRequest request) {
        try {
            this.clientAuthorityFacade.create(request.clientUid, request.authorityName);
        } catch (ClientAuthorityRepository.ClientAuthorityAlreadyExistsException e) {
            throw new ClientAuthorityAlreadyExistsResponse();
        }
        return new ClientAuthorityCreatedResponse();
    }

    @GetMapping("/{clientUid}/authority")
    public List<ClientAuthorityFacade.ClientAuthorityDTO> getAllClientAuthorities(@PathVariable("clientUid") UUID clientUid) {
        return this.clientAuthorityFacade.getAll(clientUid);
    }

    @DeleteMapping("/{clientUid}/authority/{authority}")
    public ClientAuthorityDeletedResponse deleteClientAuthority(@PathVariable("clientUid") UUID clientUid, @PathVariable("authority") String authority) {
        try {
            this.clientAuthorityFacade.delete(clientUid, authority);
        } catch (ClientAuthorityFacade.ClientAuthorityNotFoundException e) {
            throw new ClientAuthorityAlreadyExistsResponse();
        }
        return new ClientAuthorityDeletedResponse();
    }

    @GetMapping("/{clientUid}/authority/{name}")
    public ClientAuthorityFacade.ClientAuthorityDTO getClientAuthority(@PathVariable("clientUid") UUID clientUid, @PathVariable("name") String name) {
        return this.clientAuthorityFacade.get(clientUid, name)
                .orElseThrow(ClientAuthorityNotFoundResponse::new);
    }

    private record CreateClientAuthorityRequest(UUID clientUid, String authorityName) {
    }

    private static class ClientAuthorityDeletedResponse extends SuccessResponse {
    }

    private static class ClientAuthorityCreatedResponse extends SuccessResponse {
    }

    private static class ClientAuthorityNotFoundResponse extends NotFoundResponse {
    }

    private static class ClientAuthorityAlreadyExistsResponse extends AlreadyExistsResponse {
    }

}
