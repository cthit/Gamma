package it.chalmers.gamma.controller;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.FKITSuperGroup;
import it.chalmers.gamma.response.GetGroupsResponse;
import it.chalmers.gamma.response.GetSuperGroupResponse;
import it.chalmers.gamma.response.GroupDoesNotExistResponse;
import it.chalmers.gamma.response.GroupsResponse;
import it.chalmers.gamma.service.FKITService;
import it.chalmers.gamma.service.FKITSuperGroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/superGroups")
public class SuperGroupController {

    private final FKITSuperGroupService fkitSuperGroupService;
    private final FKITService fkitService;


    public SuperGroupController(FKITSuperGroupService fkitSuperGroupService, FKITService fkitService) {
        this.fkitSuperGroupService = fkitSuperGroupService;
        this.fkitService = fkitService;
    }

    @RequestMapping(value = "/{id}/subgroups", method = RequestMethod.GET)
    public ResponseEntity<List<FKITGroup>> getAllSubGroups(@PathVariable("id") String id){
        FKITSuperGroup superGroup = this.fkitSuperGroupService.getGroup(UUID.fromString(id));
        if(superGroup == null){
            throw new GroupDoesNotExistResponse();
        }
        List<FKITGroup> groups = this.fkitService.getGroupsInSuperGroup(superGroup);
        return new GroupsResponse(groups);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<FKITSuperGroup>> getAllSuperGroups() {
        return new GetGroupsResponse(this.fkitSuperGroupService.getAllGroups());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<FKITSuperGroup> getSuperGroup(@PathVariable("id") String id) {
        if (!this.fkitSuperGroupService.groupExists(UUID.fromString(id))) {
            throw new GroupDoesNotExistResponse();
        }
        return new GetSuperGroupResponse(this.fkitSuperGroupService.getGroup(UUID.fromString(id)));
    }
}
