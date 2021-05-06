package it.chalmers.gamma.api;

import it.chalmers.gamma.internal.authoritylevel.service.AuthorityLevelName;
import it.chalmers.gamma.internal.group.service.GroupDTO;
import it.chalmers.gamma.internal.group.service.GroupFinder;
import it.chalmers.gamma.internal.group.service.GroupId;
import it.chalmers.gamma.internal.membership.service.MembershipDTO;
import it.chalmers.gamma.internal.membership.service.MembershipFinder;
import it.chalmers.gamma.internal.supergroup.service.SuperGroupHasGroupsException;
import it.chalmers.gamma.internal.supergroup.service.SuperGroupId;
import it.chalmers.gamma.internal.user.service.UserDTO;
import it.chalmers.gamma.internal.user.service.UserId;
import it.chalmers.gamma.internal.user.service.UserRestrictedDTO;
import it.chalmers.gamma.util.domain.Cid;
import it.chalmers.gamma.util.domain.GroupPost;
import it.chalmers.gamma.util.domain.GroupWithMembers;
import it.chalmers.gamma.util.domain.UserPost;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
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

    private final GroupFinder groupFinder;
    private final MembershipFinder membershipFinder;

    public ChalmersitApiController(GroupFinder groupFinder,
                                   MembershipFinder membershipFinder) {
        this.groupFinder = groupFinder;
        this.membershipFinder = membershipFinder;
    }

    // add /users/me


    @GetMapping("/superGroups/{id}/active")
    public List<GroupWithMembers> getActiveSuperGroupsWithMembers(@PathVariable("id") SuperGroupId id) {
        try {
            return this.groupFinder.getGroupsBySuperGroup(id)
                    .stream()
                    .map(this::toGroupWithMembers)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (EntityNotFoundException e) {
            throw new SuperGroupDoesNotExistResponse();
        }
    }

    private GroupWithMembers toGroupWithMembers(GroupDTO group) {
        try {
            return new GroupWithMembers(
                    group,
                    this.membershipFinder.getMembershipsByGroup(group.id())
                            .stream()
                            .map(membership -> new UserPost(
                                    new UserRestrictedDTO(membership.user()),
                                    membership.post())
                            )
                            .collect(Collectors.toList())
            );
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    private static class SuperGroupDoesNotExistResponse extends ErrorResponse {
        private SuperGroupDoesNotExistResponse() {
            super(HttpStatus.NOT_FOUND);
        }
    }
}
