package it.chalmers.gamma.adapter.primary.external.v1;

import it.chalmers.gamma.app.facade.internal.GroupFacade;
import it.chalmers.gamma.app.facade.internal.MeFacade;
import it.chalmers.gamma.app.facade.internal.SuperGroupFacade;
import it.chalmers.gamma.app.facade.internal.UserFacade;
import it.chalmers.gamma.app.domain.user.UserId;
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

    private final ClientApiV1Mapper mapper;

    private final UserFacade userFacade;
    private final GroupFacade groupFacade;
    private final SuperGroupFacade superGroupFacade;
    private final MeFacade meFacade;

    public ClientApiV1Controller(ClientApiV1Mapper mapper,
                                 UserFacade userFacade,
                                 GroupFacade groupFacade,
                                 SuperGroupFacade superGroupFacade,
                                 MeFacade meFacade) {
        this.mapper = mapper;
        this.userFacade = userFacade;
        this.groupFacade = groupFacade;
        this.superGroupFacade = superGroupFacade;
        this.meFacade = meFacade;
    }

    public record Group() {
    }

    @GetMapping("/groups")
    public List<Group> getGroups() {
        return this.groupFacade.getAll()
                .stream()
                .map(this.mapper::map)
                .toList();
    }

    public record SuperGroup() {
    }

    @GetMapping("/superGroups")
    public List<SuperGroup> getSuperGroups() {
        return this.superGroupFacade.getAllSuperGroups()
                .stream()
                .map(this.mapper::map)
                .toList();
    }

    public record User() {
    }

    @GetMapping("/users")
    public List<User> getUsersForClient() {
        return this.userFacade.getAllByClientAccepting()
                .stream()
                .map(this.mapper::map)
                .toList();
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable("id") UserId id) {
        return null;
    }

    @GetMapping("/users/{id}/avatar")
    public void getUserAvatar(@PathVariable("id") UserId id) throws IOException { }

    public record Me(String nick) {
    }

    @GetMapping(value = "/users/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public Me getMe() {
        return this.mapper.map(this.meFacade.getMe());
    }

    private static class UserNotFoundResponse extends NotFoundResponse { }

}
