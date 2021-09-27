package it.chalmers.gamma.adapter.primary.api.v1;

import it.chalmers.gamma.app.facade.GroupFacade;
import it.chalmers.gamma.app.facade.SuperGroupFacade;
import it.chalmers.gamma.app.facade.UserFacade;
import it.chalmers.gamma.app.domain.user.UserId;
import it.chalmers.gamma.util.response.NotFoundResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * The API must not be altered since it's used by a lot of different clients.
 * If you need changes, then create a new version of the API.
 */
@RestController
@RequestMapping(ClientApiV1Controller.URI)
public class ClientApiV1Controller {

    public static final String URI = "/client/v1";

    private final ClientApiV1Mapper mapper;

    private final UserFacade userFacade;
    private final GroupFacade groupFacade;
    private final SuperGroupFacade superGroupFacade;

    public ClientApiV1Controller(ClientApiV1Mapper mapper,
                                 UserFacade userFacade,
                                 GroupFacade groupFacade,
                                 SuperGroupFacade superGroupFacade) {
        this.mapper = mapper;
        this.userFacade = userFacade;
        this.groupFacade = groupFacade;
        this.superGroupFacade = superGroupFacade;
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
//        try {
//            return new User(this.groupDTOuserService.get(id));
//        } catch (UserService.UserNotFoundException e) {
//            throw new UserNotFoundResponse();
//    }
        return null;
    }

    @GetMapping("/users/{id}/avatar")
    public void getUserAvatar(@PathVariable("id") UserId id, HttpServletResponse response) throws IOException { }

    public record Me() {
    }

    @GetMapping("/users/me")
    public Me getMe() {
//        UserDetailsImpl userDetails = UserUtils.getUserDetails();
//        List<GroupPost> groups = this.membershipService.getMembershipsByUser(userDetails.getUser().id())
//                .stream()
//                .map(membership -> new GroupPost(membership.post(), membership.group()))
//                .collect(Collectors.toList());
//
//        return new GetMeResponse(userDetails.getUser(), groups, userDetails.getAuthorities());
        return null;
    }

    private static class UserNotFoundResponse extends NotFoundResponse { }

}
