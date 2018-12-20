package it.chalmers.gamma.controller.admin;

import it.chalmers.gamma.db.entity.ITClient;
import it.chalmers.gamma.requests.AddITClientRequest;
import it.chalmers.gamma.response.ITClientAdded;
import it.chalmers.gamma.service.ITClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
}
