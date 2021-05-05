package it.chalmers.gamma.domain.group.controller;

import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.domain.group.service.GroupId;
import it.chalmers.gamma.domain.group.service.GroupDTO;
import it.chalmers.gamma.domain.group.service.GroupFinder;
import it.chalmers.gamma.domain.membership.service.MembershipRestrictedDTO;
import it.chalmers.gamma.domain.membership.service.MembershipFinder;

import java.util.List;

import it.chalmers.gamma.domain.membership.service.MembershipRestrictedFinder;
import it.chalmers.gamma.util.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/groups")
public final class GroupController {

    private final GroupFinder groupFinder;
    private final MembershipRestrictedFinder membershipRestrictedFinder;

    public GroupController(
            GroupFinder groupFinder,
            MembershipRestrictedFinder membershipRestrictedFinder) {
        this.groupFinder = groupFinder;
        this.membershipRestrictedFinder = membershipRestrictedFinder;
    }

    private record GetGroupResponse(GroupDTO group, List<MembershipRestrictedDTO> groupMembers) {}

    @GetMapping("/{id}")
    public GetGroupResponse getGroup(@PathVariable("id") GroupId id) {
        try {
            GroupDTO group = this.groupFinder.get(id);
            List<MembershipRestrictedDTO> members = this.membershipRestrictedFinder
                    .getRestrictedMembershipsInGroup(group.id());

            return new GetGroupResponse(group, members);
        } catch (EntityNotFoundException e) {
            throw new GroupNotFoundResponse();
        }
    }

    private static class GroupNotFoundResponse extends ErrorResponse {
        private GroupNotFoundResponse() {
            super(HttpStatus.NOT_FOUND);
        }
    }

}
