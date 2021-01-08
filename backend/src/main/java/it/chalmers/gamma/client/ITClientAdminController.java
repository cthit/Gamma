package it.chalmers.gamma.client;

import it.chalmers.gamma.domain.access.ITClientDTO;
import it.chalmers.gamma.client.response.ClientAddedResponse;
import it.chalmers.gamma.client.response.ClientAddedResponse.ClientAddedResponseObject;
import it.chalmers.gamma.client.response.ClientEditedResponse;
import it.chalmers.gamma.client.response.GetAllClientsResponse;
import it.chalmers.gamma.client.response.GetAllClientsResponse.GetAllClientResponseObject;
import it.chalmers.gamma.client.response.GetITClientResponse;
import it.chalmers.gamma.client.response.GetITClientResponse.GetITClientResponseObject;
import it.chalmers.gamma.client.response.ITClientDoesNotExistException;
import it.chalmers.gamma.client.response.ITClientRemovedResponse;

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
    public ClientAddedResponseObject addITClient(@RequestBody AddITClientRequest request) {
        return new ClientAddedResponse(this.itClientService.createITClient(
                request.getName(),
                request.getDescription(),
                request.getWebServerRedirectUri(),
                request.isAutoApprove()
        )
                .getClientSecret()).toResponseObject();
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
        return new ITClientDTO(
                request.getWebServerRedirectUri(),
                request.getName(),
                request.getDescription(),
                request.isAutoApprove()
        );
    }
}
