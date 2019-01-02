package it.chalmers.gamma.controller.admin;

import it.chalmers.gamma.db.entity.ITClient;
import it.chalmers.gamma.requests.AddITClientRequest;
import it.chalmers.gamma.response.EditedClientResponse;
import it.chalmers.gamma.response.GetAllClientsResponse;
import it.chalmers.gamma.response.GetITClient;
import it.chalmers.gamma.response.ITClientAdded;
import it.chalmers.gamma.response.ITClientRemovedResponse;
import it.chalmers.gamma.response.NoSuchClientExistsResponse;
import it.chalmers.gamma.service.ITClientService;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/clients")
public class ITClientAdminController {

    private final ITClientService itClientService;

    public ITClientAdminController(ITClientService itClientService) {
        this.itClientService = itClientService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> addITClient(@RequestBody AddITClientRequest request) {
        this.itClientService.createITClient(request);
        return new ITClientAdded();
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ITClient>> getAllOauthClients() {
        return new GetAllClientsResponse(this.itClientService.getAllClients());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public ResponseEntity<ITClient> getClient(@PathVariable("id") String id) {
        ITClient client = this.itClientService.getITClient(UUID.fromString(id));
        return new GetITClient(client);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public ResponseEntity<String> removeClient(@PathVariable("id") String id) {
        if (this.itClientService.clientExists(UUID.fromString(id))) {
            this.itClientService.removeITClient(UUID.fromString(id));
        }
        return new ITClientRemovedResponse();
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    public ResponseEntity<String> editClient(
            @PathVariable("id") String id, @RequestBody AddITClientRequest request) {
        if (this.itClientService.clientExists(UUID.fromString(id))) {
            throw new NoSuchClientExistsResponse();
        }
        this.itClientService.editClient(UUID.fromString(id), request);
        return new EditedClientResponse();
    }
}
