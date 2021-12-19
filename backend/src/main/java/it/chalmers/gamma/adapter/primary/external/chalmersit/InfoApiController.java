package it.chalmers.gamma.adapter.primary.external.chalmersit;

import it.chalmers.gamma.app.group.GroupFacade;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Going to be used by chalmers.it to display groups and their members.
 * A separate API since user info are going to be returned, and that should be used with caution.
 */
@RestController
@RequestMapping(InfoApiController.URI)
public class InfoApiController {

    public static final String URI = "/external/info";

    private final GroupFacade groupFacade;

    public InfoApiController(GroupFacade groupFacade) {
        this.groupFacade = groupFacade;
    }

    // add get all groups that's a committee and SOCIETY

    record GroupsResponse(List<GroupFacade.GroupDTO> groups) {
    }
//
//    @GetMapping("/groups")
//    GroupsResponse getGroups() {
//        return new GroupsResponse(
//                this.groupFacade
//                        .getAll()
//                        .stream()
//                        .filter(group -> )
//        );
//    }

}
