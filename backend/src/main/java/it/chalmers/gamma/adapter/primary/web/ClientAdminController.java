package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.client.ClientRepository;
import it.chalmers.gamma.domain.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.domain.apikey.ApiKeyToken;
import it.chalmers.gamma.domain.common.PrettyName;
import it.chalmers.gamma.domain.common.Text;
import it.chalmers.gamma.domain.client.ClientId;
import it.chalmers.gamma.domain.client.ClientSecret;
import it.chalmers.gamma.domain.client.Client;
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

//        Client newClient =
//                Client.create(
//                        request.webServerRedirectUri,
//                        request.autoApprove,
//                        request.prettyName,
//                        request.description,
//                        request.restrictions
//                );
//        if (request.generateApiKey) {
//            apiKeyToken = ApiKeyToken.generate();
//            this.clientFacade.createWithApiKey(newClient, clientSecret, apiKeyToken);
//        } else {
//            this.clientFacade.create(newClient, clientSecret);
//        }

        return new NewClientSecrets(clientSecret, apiKeyToken);
    }

    @PostMapping("/{clientId}/reset")
    public ClientSecret resetClientCredentials(@PathVariable("clientId") ClientId clientId) {
        ClientSecret clientSecret;

        try {
            clientSecret = this.clientFacade.resetClientSecret(clientId);
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
    public Client getClient(@PathVariable("clientId") ClientId id) {
        return this.clientFacade.get(id).orElseThrow(ClientNotFoundResponse::new);
    }

    @DeleteMapping("/{clientId}")
    public ClientDeletedResponse deleteClient(@PathVariable("clientId") ClientId id) {
        try {
            this.clientFacade.delete(id);
            return new ClientDeletedResponse();
        } catch (ClientRepository.ClientNotFoundException e) {
            throw new ClientNotFoundResponse();
        }
    }

    private static class ClientDeletedResponse extends SuccessResponse { }

    public static class ClientNotFoundResponse extends NotFoundResponse { }
    
}
