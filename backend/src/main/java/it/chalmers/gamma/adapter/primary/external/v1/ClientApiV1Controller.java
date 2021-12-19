package it.chalmers.gamma.adapter.primary.external.v1;

import it.chalmers.gamma.app.group.GroupFacade;
import it.chalmers.gamma.app.user.MeFacade;
import it.chalmers.gamma.app.supergroup.SuperGroupFacade;
import it.chalmers.gamma.app.user.UserFacade;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.util.response.NotFoundResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * The API must not be altered since it's used by a lot of different clients.
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

    public record SuperGroup() {
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
    public UserFacade.UserDTO getUser(@PathVariable("id") UserId id) {
        return null;
    }

    @GetMapping("/users/{id}/avatar")
    public void getUserAvatar(@PathVariable("id") UserId id) throws IOException { }

    private static class UserNotFoundResponse extends NotFoundResponse { }

}
