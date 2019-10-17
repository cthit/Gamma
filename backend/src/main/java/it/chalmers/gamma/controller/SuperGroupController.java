package it.chalmers.delta.controller;

import it.chalmers.delta.db.entity.FKITGroup;
import it.chalmers.delta.db.entity.FKITGroupToSuperGroup;
import it.chalmers.delta.db.entity.FKITSuperGroup;
import it.chalmers.delta.db.entity.ITUser;
import it.chalmers.delta.db.serializers.FKITGroupSerializer;
import it.chalmers.delta.db.serializers.ITUserSerializer;
import it.chalmers.delta.response.GetGroupsResponse;
import it.chalmers.delta.response.GetSuperGroupResponse;
import it.chalmers.delta.response.GroupDoesNotExistResponse;
import it.chalmers.delta.response.GroupsResponse;
import it.chalmers.delta.service.FKITGroupToSuperGroupService;
import it.chalmers.delta.service.FKITSuperGroupService;
import it.chalmers.delta.service.GroupWebsiteService;
import it.chalmers.delta.service.MembershipService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.simple.JSONObject;
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
    private final GroupWebsiteService groupWebsiteService;
    private final MembershipService membershipService;

    public SuperGroupController(FKITSuperGroupService fkitSuperGroupService,
                                FKITGroupToSuperGroupService fkitGroupToSuperGroupService,
                                GroupWebsiteService groupWebsiteService,
                                MembershipService membershipService) {
        this.fkitSuperGroupService = fkitSuperGroupService;
        this.fkitGroupToSuperGroupService = fkitGroupToSuperGroupService;
        this.groupWebsiteService = groupWebsiteService;
        this.membershipService = membershipService;
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

    @RequestMapping(value = "/{id}/active", method = RequestMethod.GET)
    public List<JSONObject> getActiveGroup(@PathVariable("id") String id) {
        if (!this.fkitSuperGroupService.groupExists(UUID.fromString(id))) {
            throw new GroupDoesNotExistResponse();
        }
        FKITSuperGroup superGroup = this.fkitSuperGroupService.getGroup(UUID.fromString(id));
        List<FKITGroup> groups = this.fkitGroupToSuperGroupService.getActiveGroups(superGroup);
        FKITGroupSerializer serializer = new FKITGroupSerializer(FKITGroupSerializer.Properties.getAllProperties());
        ITUserSerializer userSerializer = new ITUserSerializer(ITUserSerializer.Properties.getAllProperties());
        List<JSONObject> serializedGroups = new ArrayList<>();
        for (FKITGroup group : groups) {
            List<ITUser> users = this.membershipService.getUsersInGroup(group);
            List<JSONObject> serializedUsers = new ArrayList<>();
            for (ITUser user : users) {
                serializedUsers.add(userSerializer.serialize(user, null, null));
            }
            serializedGroups.add(serializer.serialize(group, serializedUsers, this.groupWebsiteService
                    .getWebsitesOrdered(this.groupWebsiteService.getWebsites(group)), null));
        }
        return serializedGroups;
    }
}
