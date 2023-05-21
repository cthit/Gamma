package it.chalmers.gamma.adapter.primary.internal;

import it.chalmers.gamma.app.client.ClientFacade;
import it.chalmers.gamma.util.response.NotFoundResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/admin/client")
public final class ClientAdminController {

    private final ClientFacade clientFacade;

    public ClientAdminController(ClientFacade clientFacade) {
        this.clientFacade = clientFacade;
    }

    @PostMapping()
    public ClientFacade.ClientAndApiKeySecrets addClient(@RequestBody CreateClientRequest request) {
        return this.clientFacade.create(
                new ClientFacade.NewClient(
                        request.webServerRedirectUrl,
                        request.prettyName,
                        request.svDescription,
                        request.enDescription,
                        request.generateApiKey,
                        request.emailScope
                )
        );
    }

    @PostMapping("/{clientUid}/reset")
    public String resetClientCredentials(@PathVariable("clientUid") String clientUid) {
        String clientSecret;

        try {
            clientSecret = this.clientFacade.resetClientSecret(clientUid);
        } catch (ClientFacade.ClientNotFoundException e) {
            throw new ClientNotFoundResponse();
        }

        return clientSecret;
    }

    @GetMapping()
    public List<ClientFacade.ClientDTO> getClients() {
        return this.clientFacade.getAll();
    }

    @GetMapping("/{clientUid}")
    public ClientFacade.ClientDTO getClient(@PathVariable("clientUid") String clientUid) {
        return this.clientFacade.get(clientUid).orElseThrow(ClientNotFoundResponse::new);
    }

    @DeleteMapping("/{clientUid}")
    public ClientDeletedResponse deleteClient(@PathVariable("clientUid") String clientUid) {
        try {
            this.clientFacade.delete(clientUid);
            return new ClientDeletedResponse();
        } catch (ClientFacade.ClientNotFoundException e) {
            throw new ClientNotFoundResponse();
        }
    }

    private record CreateClientRequest(String webServerRedirectUrl,
                                       String prettyName,
                                       String svDescription,
                                       String enDescription,
                                       boolean generateApiKey,
                                       List<String> restrictions,
                                       boolean emailScope
    ) {
    }

    private static class ClientDeletedResponse extends SuccessResponse {
    }

    public static class ClientNotFoundResponse extends NotFoundResponse {
    }

}
