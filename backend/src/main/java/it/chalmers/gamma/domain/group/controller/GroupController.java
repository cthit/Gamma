package it.chalmers.gamma.domain.group.controller;

import it.chalmers.gamma.domain.group.GroupId;
import it.chalmers.gamma.domain.group.controller.response.*;
import it.chalmers.gamma.domain.group.data.GroupDTO;
import it.chalmers.gamma.domain.group.exception.GroupNotFoundException;
import it.chalmers.gamma.domain.group.service.GroupFinder;
import it.chalmers.gamma.domain.membership.service.MembershipFinder;
import it.chalmers.gamma.domain.group.controller.response.GetActiveGroupsResponse.GetActiveGroupResponseObject;
import it.chalmers.gamma.domain.group.controller.response.GetGroupResponse.GetGroupResponseObject;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/groups")
public final class GroupController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupController.class);

    private final GroupFinder groupFinder;
    private final MembershipFinder membershipFinder;

    public GroupController(
            GroupFinder groupFinder,
            MembershipFinder membershipFinder) {
        this.groupFinder = groupFinder;
        this.membershipFinder = membershipFinder;
    }

    @GetMapping("/{id}")
    public GetGroupResponseObject getGroup(@PathVariable("id") GroupId id) {
        try {
            GroupDTO group = this.groupFinder.getGroup(id);

            return new GetGroupResponse(
                    group,
                    this.membershipFinder.getRestrictedMembershipsInGroup(group)
            ).toResponseObject();
        } catch (GroupNotFoundException e) {
            throw new GroupDoesNotExistResponse();
        }
    }


    @GetMapping()
    public GetAllGroupsResponse getGroups() {
        List<GetGroupResponse> responses = this.groupFinder.getGroups()
                .stream()
                .map(g -> new GetGroupResponse(
                        g,
                        this.membershipFinder.getRestrictedMembershipsInGroup(g))
                ).collect(Collectors.toList());

        return new GetAllGroupsResponse(responses);
    }

    @GetMapping("/active")
    public GetActiveGroupResponseObject getActiveGroups() {
        return new GetActiveGroupsResponse(this.membershipFinder.getActiveGroupsWithMemberships()).toResponseObject();
    }

}
