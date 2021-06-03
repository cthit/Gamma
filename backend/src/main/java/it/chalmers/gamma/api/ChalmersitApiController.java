package it.chalmers.gamma.api;

import it.chalmers.gamma.internal.group.service.GroupDTO;
import it.chalmers.gamma.domain.SuperGroupId;
import it.chalmers.gamma.internal.group.service.GroupService;
import it.chalmers.gamma.internal.membership.service.MembershipService;
import it.chalmers.gamma.internal.user.service.UserRestrictedDTO;
import it.chalmers.gamma.domain.GroupWithMembers;
import it.chalmers.gamma.domain.UserPost;
import it.chalmers.gamma.util.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/chalmersit")
public class ChalmersitApiController {

    private final GroupService groupService;
    private final MembershipService membershipService;

    public ChalmersitApiController(GroupService groupService,
                                   MembershipService membershipService) {
        this.groupService = groupService;
        this.membershipService = membershipService;
    }

    // add /users/me

    @GetMapping("/superGroups/{id}/active")
    public List<GroupWithMembers> getActiveSuperGroupsWithMembers(@PathVariable("id") SuperGroupId id) {
        return this.groupService.getGroupsBySuperGroup(id)
                .stream()
                .map(this::toGroupWithMembers)
                .collect(Collectors.toList());
    }

    private GroupWithMembers toGroupWithMembers(GroupDTO group) {
        return new GroupWithMembers(
                group,
                this.membershipService.getMembershipsByGroup(group.id())
                        .stream()
                        .map(membership -> new UserPost(
                                new UserRestrictedDTO(membership.user()),
                                membership.post())
                    )
                        .collect(Collectors.toList())
        );
    }

    private static class SuperGroupDoesNotExistResponse extends ErrorResponse {
        private SuperGroupDoesNotExistResponse() {
            super(HttpStatus.NOT_FOUND);
        }
    }
}
