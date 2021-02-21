package it.chalmers.gamma.domain.client.controller;

import it.chalmers.gamma.domain.IDsNotMatchingException;
import it.chalmers.gamma.domain.client.ClientId;
import it.chalmers.gamma.domain.client.ClientSecret;
import it.chalmers.gamma.domain.client.controller.request.EditClientInformationRequest;
import it.chalmers.gamma.domain.client.data.ClientDTO;
import it.chalmers.gamma.domain.client.exception.ClientNotFoundException;
import it.chalmers.gamma.domain.client.service.ClientFinder;
import it.chalmers.gamma.domain.client.service.ClientService;
import it.chalmers.gamma.domain.client.controller.request.AddClientRequest;
import it.chalmers.gamma.domain.client.controller.response.ClientAddedResponse;
import it.chalmers.gamma.domain.client.controller.response.ClientAddedResponse.ClientAddedResponseObject;
import it.chalmers.gamma.domain.client.controller.response.ClientEditedResponse;
import it.chalmers.gamma.domain.client.controller.response.GetAllClientsResponse;
import it.chalmers.gamma.domain.client.controller.response.GetAllClientsResponse.GetAllClientResponseObject;
import it.chalmers.gamma.domain.client.controller.response.GetClientResponse;
import it.chalmers.gamma.domain.client.controller.response.GetClientResponse.GetClientResponseObject;
import it.chalmers.gamma.domain.client.controller.response.ClientDoesNotExistResponse;
import it.chalmers.gamma.domain.client.controller.response.ClientRemovedResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/clients")
public class ClientAdminController {

    private final ClientFinder clientFinder;
    private final ClientService clientService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientAdminController.class);

    public ClientAdminController(ClientFinder clientFinder,
                                 ClientService clientService) {
        this.clientFinder = clientFinder;
        this.clientService = clientService;
    }

    @PostMapping()
    public ClientAddedResponseObject addITClient(@RequestBody AddClientRequest request) {
        ClientSecret clientSecret = new ClientSecret();

        this.clientService.createClient(
                new ClientDTO(
                        new ClientId(),
                        clientSecret,
                        request.getWebServerRedirectUri(),
                        request.isAutoApprove(),
                        request.getName(),
                        request.getDescription()
                )
        );

        return new ClientAddedResponse(clientSecret).toResponseObject();
    }

    @GetMapping()
    public GetAllClientResponseObject getClients() {
        return new GetAllClientsResponse(this.clientFinder.getClients()).toResponseObject();
    }

    @GetMapping("/{clientId}")
    public GetClientResponseObject getClient(@PathVariable("clientId") ClientId id) {
        try {
            return new GetClientResponse(this.clientFinder.getClient(id)).toResponseObject();
        } catch (ClientNotFoundException e) {
            LOGGER.error("Client not found", e);
            throw new ClientDoesNotExistResponse();
        }
    }

    @DeleteMapping("/{clientId}")
    public ClientRemovedResponse removeClient(@PathVariable("clientId") ClientId id) {
        try {
            this.clientService.removeClient(id);
            return new ClientRemovedResponse();
        } catch (ClientNotFoundException e) {
            LOGGER.error("Client not found", e);
            throw new ClientDoesNotExistResponse();
        }
    }

    @PutMapping("/{clientId}")
    public ClientEditedResponse editClient(@PathVariable("clientId") ClientId clientId, @RequestBody EditClientInformationRequest request) {
        try {
            this.clientService.editClient(clientId, request.getDescription());
            return new ClientEditedResponse();
        } catch (ClientNotFoundException e) {
            LOGGER.error("Client not found", e);
            throw new ClientDoesNotExistResponse();
        }
    }

}
