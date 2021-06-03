package it.chalmers.gamma.api;

import it.chalmers.gamma.internal.group.service.GroupDTO;
import it.chalmers.gamma.internal.group.service.GroupService;
import it.chalmers.gamma.internal.membership.service.MembershipDTO;
import it.chalmers.gamma.internal.membership.service.MembershipService;
import it.chalmers.gamma.internal.user.service.UserRestrictedDTO;
import it.chalmers.gamma.domain.GroupWithMembers;
import it.chalmers.gamma.domain.UserPost;
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

    private final MembershipService membershipService;
    private final GroupService groupService;

    public GoldappsApiController(MembershipService membershipService,
                                 GroupService groupService) {
        this.membershipService = membershipService;
        this.groupService = groupService;
    }

    private record Goldapps(List<GroupWithMembers> allGroups) { }

    @GetMapping
    public Goldapps get() {
        return new Goldapps(
                this.groupService.getAll()
                        .stream()
                        .map(this::toGroupWithMembers)
                        .collect(Collectors.toList())
        );
    }

    private GroupWithMembers toGroupWithMembers(GroupDTO group) {
        return new GroupWithMembers(
                group,
                toUserPosts(this.membershipService.getMembershipsByGroup(group.id()))
        );
    }

    private List<UserPost> toUserPosts(List<MembershipDTO> memberships) {
        return memberships
                .stream()
                .map(membership -> new UserPost(new UserRestrictedDTO(membership.user()), membership.post()))
                .collect(Collectors.toList());
    }

}
