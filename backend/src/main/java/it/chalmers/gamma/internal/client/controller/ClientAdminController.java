package it.chalmers.gamma.internal.client.controller;

import it.chalmers.gamma.domain.AuthorityLevelName;
import it.chalmers.gamma.domain.ClientWithRestrictions;
import it.chalmers.gamma.domain.EntityName;
import it.chalmers.gamma.domain.ApiKeyToken;
import it.chalmers.gamma.domain.Text;
import it.chalmers.gamma.domain.ClientId;
import it.chalmers.gamma.domain.ClientSecret;
import it.chalmers.gamma.domain.Client;
import it.chalmers.gamma.internal.client.service.ClientService;

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
public class ClientAdminController {

    private final ClientService clientService;

    public ClientAdminController(ClientService clientService) {
        this.clientService = clientService;
    }

    private record CreateClientRequest(String webServerRedirectUri,
                                       EntityName name,
                                       boolean autoApprove,
                                       Text description,
                                       boolean generateApiKey,
                                       List<AuthorityLevelName> restrictions
    ) { }

    private record NewClientSecrets(ClientSecret clientSecret, ApiKeyToken apiKeyToken) { }

    @PostMapping()
    public NewClientSecrets addClient(@RequestBody CreateClientRequest request) {
        ClientSecret clientSecret = new ClientSecret();
        ApiKeyToken apiKeyToken = null;

        Client newClient =
                new Client(
                        new ClientId(),
                        request.webServerRedirectUri,
                        request.autoApprove,
                        request.name,
                        request.description
                );
        if (request.generateApiKey) {
            apiKeyToken = new ApiKeyToken();
            this.clientService.createWithApiKey(newClient, clientSecret, apiKeyToken, request.restrictions);
        } else {
            this.clientService.create(newClient, clientSecret, request.restrictions);
        }

        return new NewClientSecrets(clientSecret, apiKeyToken);
    }

    @PostMapping("/{clientId}/reset")
    public ClientSecret resetClientCredentials(@PathVariable("clientId") ClientId clientId) {
        ClientSecret clientSecret = new ClientSecret();

        try {
            this.clientService.resetClientSecret(clientId, clientSecret);
        } catch (ClientService.ClientNotFoundException e) {
            throw new ClientNotFoundResponse();
        }

        return clientSecret;
    }

    @GetMapping()
    public List<Client> getClients() {
        return this.clientService.getAll();
    }

    @GetMapping("/{clientId}")
    public ClientWithRestrictions getClient(@PathVariable("clientId") ClientId id) {
        try {
            return this.clientService.get(id);
        } catch (ClientService.ClientNotFoundException e) {
            throw new ClientNotFoundResponse();
        }
    }

    @DeleteMapping("/{clientId}")
    public ClientDeletedResponse deleteClient(@PathVariable("clientId") ClientId id) {
        try {
            this.clientService.delete(id);
            return new ClientDeletedResponse();
        } catch (ClientService.ClientNotFoundException e) {
            throw new ClientNotFoundResponse();
        }
    }

    private static class ClientDeletedResponse extends SuccessResponse { }

    public static class ClientNotFoundResponse extends NotFoundResponse { }
    
}
