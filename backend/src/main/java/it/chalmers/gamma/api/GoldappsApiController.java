package it.chalmers.gamma.api;

import it.chalmers.gamma.internal.group.service.GroupDTO;
import it.chalmers.gamma.internal.group.service.GroupFinder;
import it.chalmers.gamma.internal.membership.service.MembershipDTO;
import it.chalmers.gamma.internal.membership.service.MembershipFinder;
import it.chalmers.gamma.internal.user.service.UserRestrictedDTO;
import it.chalmers.gamma.util.domain.GroupWithMembers;
import it.chalmers.gamma.util.domain.UserPost;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Used by cthit/goldapps to sync Google accounts for the student division
 */
@RestController
@RequestMapping("/goldapps")
public class GoldappsApiController {

    private final MembershipFinder membershipFinder;
    private final GroupFinder groupFinder;

    public GoldappsApiController(MembershipFinder membershipFinder, GroupFinder groupFinder) {
        this.membershipFinder = membershipFinder;
        this.groupFinder = groupFinder;
    }

    private record Goldapps(List<GroupWithMembers> allGroups) { }

    @GetMapping
    public Goldapps get() {
        return new Goldapps(
                this.groupFinder.getAll()
                        .stream()
                        .map(this::toGroupWithMembers)
                        .collect(Collectors.toList())
        );
    }

    private GroupWithMembers toGroupWithMembers(GroupDTO group) {
        try {
            return new GroupWithMembers(
                    group,
                    toUserPosts(this.membershipFinder.getMembershipsByGroup(group.id()))
            );
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    private List<UserPost> toUserPosts(List<MembershipDTO> memberships) {
        return memberships
                .stream()
                .map(membership -> new UserPost(new UserRestrictedDTO(membership.user()), membership.post()))
                .collect(Collectors.toList());
    }

}
