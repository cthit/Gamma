package it.chalmers.gamma.group.controller;

import it.chalmers.gamma.group.controller.response.*;
import it.chalmers.gamma.group.dto.GroupDTO;
import it.chalmers.gamma.group.exception.GroupNotFoundException;
import it.chalmers.gamma.group.service.GroupFinder;
import it.chalmers.gamma.membership.service.MembershipFinder;
import it.chalmers.gamma.group.controller.response.GetActiveGroupsResponse.GetActiveGroupResponseObject;
import it.chalmers.gamma.group.controller.response.GetGroupResponse.GetGroupResponseObject;

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
    public GetGroupResponseObject getGroup(@PathVariable("id") UUID id) {
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
        List<GroupDTO> groups = this.groupFinder.getGroups()
                .stream()
                .filter(GroupDTO::isActive)
                .collect(Collectors.toList());

        List<GetGroupResponse> groupResponses = groups
                .stream()
                .map(g -> new GetGroupResponse(
                        g,
                        this.membershipFinder.getRestrictedMembershipsInGroup(g))
                ).collect(Collectors.toList());
        return new GetActiveGroupsResponse(groupResponses).toResponseObject();
    }

}
