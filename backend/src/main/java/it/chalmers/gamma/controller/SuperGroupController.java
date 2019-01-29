package it.chalmers.gamma.controller;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.FKITGroupToSuperGroup;
import it.chalmers.gamma.db.entity.FKITSuperGroup;
import it.chalmers.gamma.response.GetGroupsResponse;
import it.chalmers.gamma.response.GetSuperGroupResponse;
import it.chalmers.gamma.response.GroupDoesNotExistResponse;
import it.chalmers.gamma.response.GroupsResponse;
import it.chalmers.gamma.service.FKITGroupToSuperGroupService;
import it.chalmers.gamma.service.FKITService;
import it.chalmers.gamma.service.FKITSuperGroupService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/superGroups")
public class SuperGroupController {

    private final FKITSuperGroupService fkitSuperGroupService;
    private final FKITGroupToSuperGroupService fkitGroupToSuperGroupService;

    public SuperGroupController(FKITSuperGroupService fkitSuperGroupService,
                                FKITGroupToSuperGroupService fkitGroupToSuperGroupService) {
        this.fkitSuperGroupService = fkitSuperGroupService;
        this.fkitGroupToSuperGroupService = fkitGroupToSuperGroupService;
    }

    @RequestMapping(value = "/{id}/subgroups", method = RequestMethod.GET)
    public ResponseEntity<List<FKITGroup>> getAllSubGroups(@PathVariable("id") String id) {
        FKITSuperGroup superGroup = this.fkitSuperGroupService.getGroup(UUID.fromString(id));
        if (superGroup == null) {
            throw new GroupDoesNotExistResponse();
        }
        List<FKITGroupToSuperGroup> groupRelationships = this.fkitGroupToSuperGroupService.getRelationships(superGroup);
        List<FKITGroup> groups = new ArrayList<>();
        for (FKITGroupToSuperGroup group : groupRelationships) {
            groups.add(group.getId().getGroup());
        }
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
