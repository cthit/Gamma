package it.chalmers.delta.controller.admin;

import it.chalmers.delta.db.entity.ITClient;
import it.chalmers.delta.requests.AddITClientRequest;
import it.chalmers.delta.response.EditedClientResponse;
import it.chalmers.delta.response.GetAllClientsResponse;
import it.chalmers.delta.response.GetITClient;
import it.chalmers.delta.response.ITClientRemovedResponse;
import it.chalmers.delta.response.NoSuchClientExistsResponse;
import it.chalmers.delta.service.ITClientService;

import java.util.List;
import java.util.UUID;

import org.json.simple.JSONObject;
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
    public JSONObject addITClient(@RequestBody AddITClientRequest request) {
        String clientSecret = this.itClientService.createITClient(request);
        JSONObject response = new JSONObject();
        response.put("clientSecret", clientSecret);
        return response;
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
