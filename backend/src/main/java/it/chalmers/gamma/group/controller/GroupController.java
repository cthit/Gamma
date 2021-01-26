package it.chalmers.gamma.group.controller;

import it.chalmers.gamma.group.controller.response.*;
import it.chalmers.gamma.group.dto.GroupDTO;
import it.chalmers.gamma.group.exception.GroupNotFoundException;
import it.chalmers.gamma.group.service.GroupFinder;
import it.chalmers.gamma.group.service.GroupService;
import it.chalmers.gamma.membership.dto.MembershipDTO;
import it.chalmers.gamma.membership.service.MembershipFinder;
import it.chalmers.gamma.membership.dto.RestrictedMembershipDTO;
import it.chalmers.gamma.membership.service.MembershipService;
import it.chalmers.gamma.group.controller.response.GetActiveFKITGroupsResponse.GetActiveFKITGroupResponseObject;
import it.chalmers.gamma.group.controller.response.GetAllFKITGroupsMinifiedResponse.GetAllFKITGroupsMinifiedResponseObject;
import it.chalmers.gamma.group.controller.response.GetFKITGroupMinifiedResponse.GetFKITGroupMinifiedResponseObject;
import it.chalmers.gamma.group.controller.response.GetFKITGroupResponse.GetFKITGroupResponseObject;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/groups")
public final class GroupController {

    private final GroupService groupService;
    private final MembershipService membershipService;
    private final GroupFinder groupFinder;
    private final MembershipFinder membershipFinder;

    public GroupController(
            GroupService groupService,
            MembershipService membershipService,
            GroupFinder groupFinder, MembershipFinder membershipFinder) {
        this.groupService = groupService;
        this.membershipService = membershipService;
        this.groupFinder = groupFinder;
        this.membershipFinder = membershipFinder;
    }

    @GetMapping("/{id}")
    public GetFKITGroupResponseObject getGroup(@PathVariable("id") UUID id) {
        try {
            GroupDTO group = this.groupFinder.getGroup(id);

            List<MembershipDTO> minifiedMembers = this.membershipFinder.getMembershipsInGroup(group);
            return new GetFKITGroupResponse(
                    group,
                    toRestrictedMembershipDTO(minifiedMembers)
            ).toResponseObject();
        } catch (GroupNotFoundException e) {
            throw new GroupDoesNotExistResponse();
        }
    }

    @GetMapping("/minified")
    public GetAllFKITGroupsMinifiedResponseObject getGroupsMinified() {
        List<GetFKITGroupMinifiedResponse> responses = this.groupService.getGroups()
                .stream().map(g -> new GetFKITGroupMinifiedResponse(g.toMinifiedDTO())).collect(Collectors.toList());
        return new GetAllFKITGroupsMinifiedResponse(responses).toResponseObject();
    }

    @GetMapping("/{id}/minified")
    public GetFKITGroupMinifiedResponseObject getGroupMinified(@PathVariable("id") String id) {
        return new GetFKITGroupMinifiedResponse(this.groupService.getGroup(id)
                .toMinifiedDTO()).toResponseObject();
    }

    @GetMapping()
    public GetAllFKITGroupsResponse getGroups() {
        List<GetFKITGroupResponse> responses = this.groupService.getGroups()
                .stream().map(g -> new GetFKITGroupResponse(
                        g,
                        toRestrictedMembershipDTO(this.membershipService.getMembershipsInGroup(g)),
                        this.membershipService.getNoAccountMembership(g)
                )).collect(Collectors.toList());

        return new GetAllFKITGroupsResponse(responses);
    }

    @GetMapping("/active")
    public GetActiveFKITGroupResponseObject getActiveGroups() {
        List<GroupDTO> groups = this.groupService.getGroups().stream()
                .filter(GroupDTO::isActive).collect(Collectors.toList());

        List<GetFKITGroupResponse> groupResponses = groups.stream().map(g -> new GetFKITGroupResponse(
                g,
                toRestrictedMembershipDTO(this.membershipService.getMembershipsInGroup(g)),
                this.membershipService.getNoAccountMembership(g)
        )).collect(Collectors.toList());
        return new GetActiveFKITGroupsResponse(groupResponses).toResponseObject();
    }

    private List<RestrictedMembershipDTO> toRestrictedMembershipDTO(List<MembershipDTO> membershipList) {
        return membershipList
                .stream()
                .map(RestrictedMembershipDTO::new)
                .collect(Collectors.toList());
    }

}
