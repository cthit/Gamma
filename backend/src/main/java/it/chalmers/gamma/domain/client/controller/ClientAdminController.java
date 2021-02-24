package it.chalmers.gamma.domain.client.controller;

import it.chalmers.gamma.domain.EntityNotFoundException;
import it.chalmers.gamma.domain.client.domain.ClientId;
import it.chalmers.gamma.domain.client.domain.ClientSecret;
import it.chalmers.gamma.domain.client.controller.request.UpdateClientInformationRequest;
import it.chalmers.gamma.domain.client.data.dto.ClientDTO;
import it.chalmers.gamma.domain.client.service.ClientFinder;
import it.chalmers.gamma.domain.client.service.ClientService;
import it.chalmers.gamma.domain.client.controller.request.CreateClientRequest;
import it.chalmers.gamma.domain.client.controller.response.ClientCreatedResponse;
import it.chalmers.gamma.domain.client.controller.response.ClientUpdatedResponse;
import it.chalmers.gamma.domain.client.controller.response.GetAllClientResponse;
import it.chalmers.gamma.domain.client.controller.response.GetClientResponse;
import it.chalmers.gamma.domain.client.controller.response.ClientNotFoundResponse;
import it.chalmers.gamma.domain.client.controller.response.ClientDeletedResponse;

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
    public ClientCreatedResponse addITClient(@RequestBody CreateClientRequest request) {
        ClientSecret clientSecret = new ClientSecret();

        this.clientService.create(
                new ClientDTO(
                        new ClientId(),
                        clientSecret,
                        request.webServerRedirectUri,
                        request.autoApprove,
                        request.name,
                        request.description
                )
        );

        return new ClientCreatedResponse(clientSecret);
    }

    @GetMapping()
    public GetAllClientResponse getAllClient() {
        return new GetAllClientResponse(this.clientFinder.getAll());
    }

    @GetMapping("/{clientId}")
    public GetClientResponse getClient(@PathVariable("clientId") ClientId id) {
        try {
            return new GetClientResponse(this.clientFinder.get(id));
        } catch (EntityNotFoundException e) {
            throw new ClientNotFoundResponse();
        }
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

    @PutMapping("/{clientId}")
    public ClientUpdatedResponse updateClient(@PathVariable("clientId") ClientId clientId, @RequestBody UpdateClientInformationRequest request) {
        //this.clientService.update(clientId, request.description);
        return new ClientUpdatedResponse();
    }

}
