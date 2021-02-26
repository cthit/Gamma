package it.chalmers.gamma.domain.group.controller;

import it.chalmers.gamma.domain.EntityNotFoundException;
import it.chalmers.gamma.domain.group.GroupId;
import it.chalmers.gamma.domain.group.controller.response.*;
import it.chalmers.gamma.domain.group.data.GroupDTO;
import it.chalmers.gamma.domain.group.service.GroupFinder;
import it.chalmers.gamma.domain.membership.service.MembershipFinder;

import java.util.List;
import java.util.stream.Collectors;

import it.chalmers.gamma.domain.membership.service.MembershipRestrictedFinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/groups")
public final class GroupController {

    private final GroupFinder groupFinder;
    private final MembershipFinder membershipFinder;
    private final MembershipRestrictedFinder membershipRestrictedFinder;

    public GroupController(
            GroupFinder groupFinder,
            MembershipFinder membershipFinder,
            MembershipRestrictedFinder membershipRestrictedFinder) {
        this.groupFinder = groupFinder;
        this.membershipFinder = membershipFinder;
        this.membershipRestrictedFinder = membershipRestrictedFinder;
    }

    @GetMapping("/{id}")
    public GetGroupResponse getGroup(@PathVariable("id") GroupId id) {
        try {
            GroupDTO group = this.groupFinder.get(id);

            return new GetGroupResponse(
                    group,
                    this.membershipRestrictedFinder.getRestrictedMembershipsInGroup(group.getId())
            );
        } catch (EntityNotFoundException e) {
            throw new GroupNotFoundResponse();
        }
    }


    @GetMapping()
    public GetAllGroupResponse getGroups() {
        List<GetGroupResponse> responses = this.groupFinder.getAll()
                .stream()
                .map(group -> new GetGroupResponse(
                        group,
                        this.membershipRestrictedFinder.getRestrictedMembershipsInGroup(group.getId()))
                ).collect(Collectors.toList());

        return new GetAllGroupResponse(responses);
    }

    // JOIN
    @GetMapping("/active")
    public GetAllActiveGroupResponse getActiveGroups() {
        return new GetAllActiveGroupResponse();
    }

}
