package it.chalmers.gamma.internal.client.controller;

import it.chalmers.gamma.domain.EntityName;
import it.chalmers.gamma.internal.apikey.service.ApiKeyService;
import it.chalmers.gamma.domain.ApiKeyToken;
import it.chalmers.gamma.internal.client.apikey.service.ClientApiKeyService;
import it.chalmers.gamma.internal.text.service.TextDTO;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.domain.ClientId;
import it.chalmers.gamma.domain.ClientSecret;
import it.chalmers.gamma.internal.client.service.ClientDTO;
import it.chalmers.gamma.internal.client.service.ClientFinder;
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
@RequestMapping("/admin/clients")
public class ClientAdminController {

    private final ClientFinder clientFinder;
    private final ClientService clientService;
    private final ClientApiKeyService clientApiKeyService;
    private final ApiKeyService apiKeyService;

    public ClientAdminController(ClientFinder clientFinder,
                                 ClientService clientService,
                                 ClientApiKeyService clientApiKeyService,
                                 ApiKeyService apiKeyService) {
        this.clientFinder = clientFinder;
        this.clientService = clientService;
        this.clientApiKeyService = clientApiKeyService;
        this.apiKeyService = apiKeyService;
    }

    private record CreateClientRequest(String webServerRedirectUri, EntityName name, boolean autoApprove, TextDTO description, boolean generateApiKey) { }

    private record NewClientSecrets(ClientSecret clientSecret, ApiKeyToken apiKeyToken) { }

    @PostMapping()
    public ClientSecret addITClient(@RequestBody CreateClientRequest request) {
        ClientSecret clientSecret = new ClientSecret();
        ApiKeyToken apiKeyToken = null;

        ClientDTO newClient =
                new ClientDTO(
                        new ClientId(),
                        clientSecret,
                        request.webServerRedirectUri,
                        request.autoApprove,
                        request.name,
                        request.description
                );
        if (request.generateApiKey) {
            apiKeyToken = new ApiKeyToken();
            this.clientService.createWithApiKey(newClient, apiKeyToken);
        } else {
            this.clientService.create(newClient);
        }

        return clientSecret;
    }

    @GetMapping()
    public List<ClientDTO> getAllClient() {
        return this.clientFinder.getAll();
    }

    @DeleteMapping("/{clientId}")
    public ClientDeletedResponse deleteClient(@PathVariable("clientId") ClientId id) {
        try {
            this.clientService.delete(id);
            return new ClientDeletedResponse();
        } catch (EntityNotFoundException e) {
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
