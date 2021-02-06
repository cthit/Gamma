package it.chalmers.gamma.client.controller;

import it.chalmers.gamma.client.dto.ClientDTO;
import it.chalmers.gamma.client.service.ClientService;
import it.chalmers.gamma.client.controller.request.AddClientRequest;
import it.chalmers.gamma.client.controller.response.ClientAddedResponse;
import it.chalmers.gamma.client.controller.response.ClientAddedResponse.ClientAddedResponseObject;
import it.chalmers.gamma.client.controller.response.ClientEditedResponse;
import it.chalmers.gamma.client.controller.response.GetAllClientsResponse;
import it.chalmers.gamma.client.controller.response.GetAllClientsResponse.GetAllClientResponseObject;
import it.chalmers.gamma.client.controller.response.GetClientResponse;
import it.chalmers.gamma.client.controller.response.GetClientResponse.GetClientResponseObject;
import it.chalmers.gamma.client.controller.response.ClientDoesNotExistException;
import it.chalmers.gamma.client.controller.response.ClientRemovedResponse;

import java.util.UUID;

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

    private final ClientService clientService;

    public ClientAdminController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping()
    public ClientAddedResponseObject addITClient(@RequestBody AddClientRequest request) {
        return new ClientAddedResponse(this.clientService.createITClient(
                request.getName(),
                request.getDescription(),
                request.getWebServerRedirectUri(),
                request.isAutoApprove()
        )
                .getClientSecret()).toResponseObject();
    }

    @GetMapping()
    public GetAllClientResponseObject getAllOauthClients() {
        return new GetAllClientsResponse(this.clientService.getAllClients()).toResponseObject();
    }

    @GetMapping("/{id}")
    public GetClientResponseObject getClient(@PathVariable("id") UUID id) {
        ClientDTO client = this.clientService.getITClient(UUID.fromString(id));
        return new GetClientResponse(client).toResponseObject();
    }

    @DeleteMapping("/{id}")
    public ClientRemovedResponse removeClient(@PathVariable("id") UUID id) {
        if (!this.clientService.clientExists(id)) {
            throw new ClientDoesNotExistException();
        }
        this.clientService.removeITClient(UUID.fromString(id));
        return new ClientRemovedResponse();
    }

    @PutMapping("/{id}")
    public ClientEditedResponse editClient(
            @PathVariable("id") UUID id, @RequestBody AddClientRequest request) {
        if (this.clientService.clientExists(id)) {
            throw new ClientDoesNotExistException();
        }
        this.clientService.editClient(UUID.fromString(id), responseToDTO(request));
        return new ClientEditedResponse();
    }

    private ClientDTO responseToDTO(AddClientRequest request) {
        return new ClientDTO.ClientDTOBuilder().setId(request.getWebServerRedirectUri()).setClientId(request.getName()).setClientSecret(request.getDescription()).setWebServerRedirectUri(request.isAutoApprove()).createClientDTO();
    }
}
