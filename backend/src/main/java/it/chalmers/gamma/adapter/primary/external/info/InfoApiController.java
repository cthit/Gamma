package it.chalmers.gamma.adapter.primary.external.info;

import it.chalmers.gamma.app.group.GroupFacade;
import it.chalmers.gamma.app.user.UserFacade;
import it.chalmers.gamma.util.response.NotFoundResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Going to be used by chalmers.it to display groups and their members.
 * A separate API since user info are going to be returned, and that should be used with caution.
 */
@RestController
@RequestMapping(InfoApiController.URI)
public class InfoApiController {

    public static final String URI = "/external/info";

    private final GroupFacade groupFacade;
    private final UserFacade userFacade;

    public InfoApiController(GroupFacade groupFacade, UserFacade userFacade) {
        this.groupFacade = groupFacade;
        this.userFacade = userFacade;
    }

    record GroupsResponse(List<GroupFacade.GroupWithMembersDTO> groups) { }

    @GetMapping("/users/{id}")
    public UserFacade.UserWithGroupsDTO getUser(@PathVariable("id") UUID id) {
        return this.userFacade.get(id)
                .orElseThrow(UserNotFoundResponse::new);
    }

    @GetMapping("/groups")
    public GroupsResponse getGroups() {
        return new GroupsResponse(this.groupFacade.getAllForInfoApi());
    }

    private static class UserNotFoundResponse extends NotFoundResponse { }

}
