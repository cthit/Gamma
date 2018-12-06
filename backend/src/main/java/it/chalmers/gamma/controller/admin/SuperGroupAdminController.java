package it.chalmers.gamma.controller.admin;

import it.chalmers.gamma.db.entity.FKITSuperGroup;
import it.chalmers.gamma.requests.CreateSuperGroupRequest;
import it.chalmers.gamma.response.FKITSuperGroupCreatedResponse;
import it.chalmers.gamma.response.GetGroupsResponse;
import it.chalmers.gamma.response.GroupAlreadyExistsResponse;
import it.chalmers.gamma.service.FKITSuperGroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
