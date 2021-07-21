package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.domain.AuthorityLevelName;
import it.chalmers.gamma.app.domain.ClientWithRestrictions;
import it.chalmers.gamma.app.domain.ApiKeyToken;
import it.chalmers.gamma.app.domain.PrettyName;
import it.chalmers.gamma.app.domain.Text;
import it.chalmers.gamma.app.domain.ClientId;
import it.chalmers.gamma.app.domain.ClientSecret;
import it.chalmers.gamma.app.domain.Client;
import it.chalmers.gamma.app.client.ClientFacade;

import it.chalmers.gamma.util.response.NotFoundResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internal/admin/clients")
public final class ClientAdminController {

    private final ClientFacade clientFacade;

    public ClientAdminController(ClientFacade clientFacade) {
        this.clientFacade = clientFacade;
    }

    private record CreateClientRequest(String webServerRedirectUri,
                                       PrettyName prettyName,
                                       boolean autoApprove,
                                       Text description,
                                       boolean generateApiKey,
                                       List<AuthorityLevelName> restrictions
    ) { }

    private record NewClientSecrets(ClientSecret clientSecret, ApiKeyToken apiKeyToken) { }

    @PostMapping()
    public NewClientSecrets addClient(@RequestBody CreateClientRequest request) {
        ClientSecret clientSecret = ClientSecret.generate();
        ApiKeyToken apiKeyToken = null;

        Client newClient =
                new Client(
                        ClientId.generate(),
                        request.webServerRedirectUri,
                        request.autoApprove,
                        request.prettyName,
                        request.description
                );
        if (request.generateApiKey) {
            apiKeyToken = ApiKeyToken.generate();
            this.clientFacade.createWithApiKey(newClient, clientSecret, apiKeyToken, request.restrictions);
        } else {
            this.clientFacade.create(newClient, clientSecret, request.restrictions);
        }

        return new NewClientSecrets(clientSecret, apiKeyToken);
    }

    @PostMapping("/{clientId}/reset")
    public ClientSecret resetClientCredentials(@PathVariable("clientId") ClientId clientId) {
        ClientSecret clientSecret = ClientSecret.generate();

        try {
            this.clientFacade.resetClientSecret(clientId, clientSecret);
        } catch (ClientFacade.ClientNotFoundException e) {
            throw new ClientNotFoundResponse();
        }

        return clientSecret;
    }

    @GetMapping()
    public List<Client> getClients() {
        return this.clientFacade.getAll();
    }

    @GetMapping("/{clientId}")
    public ClientWithRestrictions getClient(@PathVariable("clientId") ClientId id) {
        try {
            return this.clientFacade.get(id);
        } catch (ClientFacade.ClientNotFoundException e) {
            throw new ClientNotFoundResponse();
        }
    }

    @DeleteMapping("/{clientId}")
    public ClientDeletedResponse deleteClient(@PathVariable("clientId") ClientId id) {
        try {
            this.clientFacade.delete(id);
            return new ClientDeletedResponse();
        } catch (ClientFacade.ClientNotFoundException e) {
            throw new ClientNotFoundResponse();
        }
    }

    private static class ClientDeletedResponse extends SuccessResponse { }

    public static class ClientNotFoundResponse extends NotFoundResponse { }
    
}
