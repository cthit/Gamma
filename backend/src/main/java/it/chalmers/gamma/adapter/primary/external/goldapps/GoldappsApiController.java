package it.chalmers.gamma.adapter.primary.external.goldapps;

import it.chalmers.gamma.app.group.GroupFacade;
import it.chalmers.gamma.app.group.domain.Group;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Used by cthit/goldapps to sync Google accounts for the student division.
 */
@RestController
@RequestMapping(GoldappsApiController.URI)
public class GoldappsApiController {

    public static final String URI = "/external/goldapps";

    private final GroupFacade groupFacade;

    public GoldappsApiController(GroupFacade groupFacade) {
        this.groupFacade = groupFacade;
    }

    private record Goldapps(List<Group> allGroups) { }

//    @GetMapping
//    public Goldapps get() {
//        return new Goldapps(
//                this.groupFacade.getAll()
//                        .stream()
//                        .map(this::toGroupWithMembers)
//                        .collect(Collectors.toList())
//        );
//    }
//
//    private GroupWithMembers toGroupWithMembers(Group group) {
//        return new GroupWithMembers(
//                group,
//                toUserPosts(this.membershipService.getMembershipsByGroup(group.value()))
//        );
//    }
//
//    private List<UserPost> toUserPosts(List<Membership> memberships) {
//        return memberships
//                .stream()
//                .map(membership -> new UserPost(membership.user(), membership.post()))
//                .collect(Collectors.toList());
//    }

}
