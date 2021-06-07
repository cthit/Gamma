package it.chalmers.gamma.internal.client.controller;

import it.chalmers.gamma.domain.EntityName;
import it.chalmers.gamma.internal.apikey.service.ApiKeyService;
import it.chalmers.gamma.domain.ApiKeyToken;
import it.chalmers.gamma.internal.client.apikey.service.ClientApiKeyService;
import it.chalmers.gamma.domain.Text;
import it.chalmers.gamma.domain.ClientId;
import it.chalmers.gamma.domain.ClientSecret;
import it.chalmers.gamma.domain.Client;
import it.chalmers.gamma.internal.client.service.ClientService;

import it.chalmers.gamma.util.response.ErrorResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.http.HttpStatus;
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
    private final ClientApiKeyService clientApiKeyService;
    private final ApiKeyService apiKeyService;

    public ClientAdminController(ClientService clientService,
                                 ClientApiKeyService clientApiKeyService,
                                 ApiKeyService apiKeyService) {
        this.clientService = clientService;
        this.clientApiKeyService = clientApiKeyService;
        this.apiKeyService = apiKeyService;
    }

    private record CreateClientRequest(String webServerRedirectUri, EntityName name, boolean autoApprove, Text description, boolean generateApiKey) { }

    private record NewClientSecrets(ClientSecret clientSecret, ApiKeyToken apiKeyToken) { }

    @PostMapping()
    public NewClientSecrets addITClient(@RequestBody CreateClientRequest request) {
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
            this.clientService.createWithApiKey(newClient, clientSecret, apiKeyToken);
        } else {
            this.clientService.create(newClient, clientSecret);
        }

        return new NewClientSecrets(clientSecret, apiKeyToken);
    }

    @GetMapping()
    public List<Client> getAllClient() {
        return this.clientService.getAll();
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

    public static class ClientNotFoundResponse extends ErrorResponse {
        public ClientNotFoundResponse() {
            super(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }


}
