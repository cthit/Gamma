package it.chalmers.gamma.adapter.primary.external.client;

import it.chalmers.gamma.app.group.GroupFacade;
import it.chalmers.gamma.app.supergroup.SuperGroupFacade;
import it.chalmers.gamma.app.user.MeFacade;
import it.chalmers.gamma.app.user.UserFacade;
import it.chalmers.gamma.util.response.NotFoundResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * If you need changes, then create a new version of the API.
 */
@RestController
@RequestMapping(ClientApiV1Controller.URI)
public class ClientApiV1Controller {

    public static final String URI = "/external/client/v1";

    private final UserFacade userFacade;
    private final GroupFacade groupFacade;
    private final SuperGroupFacade superGroupFacade;
    private final MeFacade meFacade;

    public ClientApiV1Controller(UserFacade userFacade,
                                 GroupFacade groupFacade,
                                 SuperGroupFacade superGroupFacade,
                                 MeFacade meFacade) {
        this.userFacade = userFacade;
        this.groupFacade = groupFacade;
        this.superGroupFacade = superGroupFacade;
        this.meFacade = meFacade;
    }

    @GetMapping("/groups")
    public List<GroupFacade.GroupDTO> getGroups() {
        return this.groupFacade.getAll();
    }

    @GetMapping("/superGroups")
    public List<SuperGroupFacade.SuperGroupDTO> getSuperGroups() {
        return this.superGroupFacade.getAllSuperGroups();
    }

    @GetMapping("/users")
    public List<UserFacade.UserDTO> getUsersForClient() {
        return this.userFacade.getAllByClientAccepting();
    }

    @GetMapping("/users/{id}")
    public UserFacade.UserWithGroupsDTO getUser(@PathVariable("id") UUID id) {
        return this.userFacade.get(id)
                .orElseThrow(UserNotFoundResponse::new);
    }

    private static class UserNotFoundResponse extends NotFoundResponse {
    }

}
