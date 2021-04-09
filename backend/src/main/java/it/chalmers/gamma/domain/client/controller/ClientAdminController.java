package it.chalmers.gamma.domain.client.controller;

import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.domain.client.service.ClientId;
import it.chalmers.gamma.domain.client.service.ClientSecret;
import it.chalmers.gamma.domain.client.service.ClientDTO;
import it.chalmers.gamma.domain.client.service.ClientFinder;
import it.chalmers.gamma.domain.client.service.ClientService;

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
