package it.chalmers.delta.controller;

import it.chalmers.delta.db.entity.FKITGroup;
import it.chalmers.delta.db.entity.ITUser;
import it.chalmers.delta.db.entity.Membership;
import it.chalmers.delta.response.GetMembershipsResponse;
import it.chalmers.delta.response.GroupDoesNotExistResponse;
import it.chalmers.delta.service.FKITGroupService;
import it.chalmers.delta.service.MembershipService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/groups")
public class GroupMemberController {

    private final FKITGroupService fkitGroupService;
    private final MembershipService membershipService;

    public GroupMemberController(FKITGroupService fkitGroupService, MembershipService membershipService) {
        this.fkitGroupService = fkitGroupService;
        this.membershipService = membershipService;
    }

    @RequestMapping(value = "/{id}/members", method = RequestMethod.GET)
    public ResponseEntity<List<Membership>> getUsersInGroup(@PathVariable("id") String id) {
        FKITGroup group = this.fkitGroupService.getGroup(UUID.fromString(id));
        if (group == null) {
            throw new GroupDoesNotExistResponse();
        }
        List<ITUser> members = this.membershipService.getUsersInGroup(group);
        List<Membership> groupMembers = new ArrayList<>();
        for (ITUser member : members) {
            groupMembers.add(this.membershipService.getMembershipByUserAndGroup(member, group));
        }
        return new GetMembershipsResponse(groupMembers);
    }
}
