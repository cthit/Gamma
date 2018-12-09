package it.chalmers.gamma.controller.admin;

import com.sun.mail.iap.Response;
import it.chalmers.gamma.db.entity.FKITSuperGroup;
import it.chalmers.gamma.requests.CreateSuperGroupRequest;
import it.chalmers.gamma.response.*;
import it.chalmers.gamma.service.FKITSuperGroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/superGroups")       // What should this URL be?
public class SuperGroupAdminController {
    private final FKITSuperGroupService fkitSuperGroupService;


    public SuperGroupAdminController(FKITSuperGroupService fkitSuperGroupService){
        this.fkitSuperGroupService = fkitSuperGroupService;

    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<FKITSuperGroup> createSuperGroup(@RequestBody CreateSuperGroupRequest request){
            if(fkitSuperGroupService.groupExists(request.getName())){
                throw new GroupAlreadyExistsResponse();
            }
            FKITSuperGroup group = fkitSuperGroupService.createSuperGroup(request);
            return new FKITSuperGroupCreatedResponse(group);
        }
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<FKITSuperGroup>> getAllSuperGroups(){
        return new GetGroupsResponse(fkitSuperGroupService.getAllGroups());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<FKITSuperGroup> getSuperGroup(@PathVariable("id") String id){
        return new GetSuperGroupResponse(fkitSuperGroupService.getGroup(UUID.fromString(id)));
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> removeSuperGroup(@PathVariable("id") String id){
        fkitSuperGroupService.removeGroup(UUID.fromString(id));
        return new GroupDeletedResponse();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<String> updateSuperGroup(@PathVariable("id") String id,
                                                   @RequestBody CreateSuperGroupRequest request){
        if(!fkitSuperGroupService.groupExists(UUID.fromString(id))){
            throw new GroupDoesNotExistResponse();
        }
        fkitSuperGroupService.updateSuperGroup(UUID.fromString(id), request);
        return new GroupEditedResponse();
    }
}
