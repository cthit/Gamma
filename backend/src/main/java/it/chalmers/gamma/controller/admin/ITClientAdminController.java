package it.chalmers.gamma.controller.admin;

import it.chalmers.gamma.domain.dto.access.ITClientDTO;
import it.chalmers.gamma.requests.AddITClientRequest;
import it.chalmers.gamma.response.client.ClientEditedResponse;
import it.chalmers.gamma.response.client.GetAllClientsResponse;
import it.chalmers.gamma.response.client.GetAllClientsResponse.GetAllClientResponseObject;
import it.chalmers.gamma.response.client.GetITClientResponse;
import it.chalmers.gamma.response.client.GetITClientResponse.GetITClientResponseObject;
import it.chalmers.gamma.response.client.ITClientDoesNotExistException;
import it.chalmers.gamma.response.client.ITClientRemovedResponse;
import it.chalmers.gamma.service.ITClientService;

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
public class ITClientAdminController {

    private final ITClientService itClientService;

    public ITClientAdminController(ITClientService itClientService) {
        this.itClientService = itClientService;
    }

    @PostMapping()
    public GetITClientResponseObject addITClient(@RequestBody AddITClientRequest request) {
        return new GetITClientResponse(this.itClientService.createITClient(responseToDTO(request))).toResponseObject();
    }

    @GetMapping()
    public GetAllClientResponseObject getAllOauthClients() {
        return new GetAllClientsResponse(this.itClientService.getAllClients()).toResponseObject();
    }

    @GetMapping("/{id}")
    public GetITClientResponseObject getClient(@PathVariable("id") String id) {
        ITClientDTO client = this.itClientService.getITClient(UUID.fromString(id));
        return new GetITClientResponse(client).toResponseObject();
    }

    @DeleteMapping("/{id}")
    public ITClientRemovedResponse removeClient(@PathVariable("id") String id) {
        if (!this.itClientService.clientExists(id)) {
            throw new ITClientDoesNotExistException();
        }
        this.itClientService.removeITClient(UUID.fromString(id));
        return new ITClientRemovedResponse();
    }

    @PutMapping("/{id}")
    public ClientEditedResponse editClient(
            @PathVariable("id") String id, @RequestBody AddITClientRequest request) {
        if (this.itClientService.clientExists(id)) {
            throw new ITClientDoesNotExistException();
        }
        this.itClientService.editClient(UUID.fromString(id), responseToDTO(request));
        return new ClientEditedResponse();
    }

    private ITClientDTO responseToDTO(AddITClientRequest request) {
        return new ITClientDTO(request.getWebServerRedirectUri(), request.getName(), request.getDescription());
    }
}
