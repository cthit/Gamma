package it.chalmers.gamma.controller.admin;

import it.chalmers.gamma.db.entity.ITClient;
import it.chalmers.gamma.requests.AddITClientRequest;
import it.chalmers.gamma.response.*;
import it.chalmers.gamma.service.ITClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/auth")
public class ITClientAdminController {
    private final ITClientService itClientService;
    public ITClientAdminController(ITClientService itClientService){
        this.itClientService = itClientService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> addITClient(@RequestBody AddITClientRequest request){
        itClientService.createITClient(request);
        return new ITClientAdded();
    }
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ITClient>> getAllOauthClients(){
        return new GetAllClientsResponse(itClientService.getAllClients());
    }
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public ResponseEntity<ITClient> getClient(@PathVariable("id") String id){
        ITClient client = itClientService.getITClient(UUID.fromString(id));
        return new GetITClient(client);
    }
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public ResponseEntity<String> removeClient(@PathVariable("id") String id){
        if(itClientService.clientExists(UUID.fromString(id)))
        itClientService.removeITClient(UUID.fromString(id));
        return new ITClientRemovedResponse();
    }
    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    public ResponseEntity<String> editClient(@PathVariable("id") String id, @RequestBody AddITClientRequest request){
        if(itClientService.clientExists(UUID.fromString(id))){
            throw new NoSuchClientExistsResponse();
        }
        itClientService.editClient(UUID.fromString(id), request);
        return new EditedClientResponse();
    }
}
