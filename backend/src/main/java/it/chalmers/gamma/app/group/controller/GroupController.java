package it.chalmers.gamma.app.group.controller;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.app.group.service.GroupService;
import it.chalmers.gamma.app.domain.GroupId;
import it.chalmers.gamma.app.domain.Group;

import java.util.List;

import it.chalmers.gamma.app.domain.Membership;
import it.chalmers.gamma.app.membership.service.MembershipService;
import it.chalmers.gamma.util.response.NotFoundResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/groups")
public final class GroupController {

    private final GroupService groupService;
    private final MembershipService membershipService;

    public GroupController(
            GroupService groupService,
            MembershipService membershipService) {
        this.groupService = groupService;
        this.membershipService = membershipService;
    }

    @GetMapping()
    public List<Group> getGroups() {
        return this.groupService.getAll();
    }

    private record GetGroupResponse(@JsonUnwrapped Group group, List<Membership> members) { }

    @GetMapping("/{id}")
    public GetGroupResponse getGroup(@PathVariable("id") GroupId id) {
        try {
            Group group = this.groupService.get(id);
            List<Membership> members = this.membershipService.getMembershipsInGroup(group.id());

            return new GetGroupResponse(group, members);
        } catch (GroupService.GroupNotFoundException e) {
            throw new GroupNotFoundResponse();
        }
    }

    private static class GroupNotFoundResponse extends NotFoundResponse { }

}
