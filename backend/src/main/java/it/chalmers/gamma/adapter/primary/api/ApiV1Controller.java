package it.chalmers.gamma.adapter.primary.api;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.app.GroupFacade;
import it.chalmers.gamma.app.SuperGroupFacade;
import it.chalmers.gamma.app.UserFacade;
import it.chalmers.gamma.app.domain.Group;
import it.chalmers.gamma.adapter.secondary.userdetails.GrantedAuthorityProxy;
import it.chalmers.gamma.app.domain.SuperGroup;
import it.chalmers.gamma.app.domain.User;
import it.chalmers.gamma.app.domain.UserId;
import it.chalmers.gamma.app.domain.GroupPost;
import it.chalmers.gamma.util.response.NotFoundResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * The API must not be altered since it's used by a lot of different clients.
 * If you need changes, then create a new version of the API.
 */
@RestController
@RequestMapping(ApiV1Controller.URI)
public class ApiV1Controller {

    public static final String URI = "/v1";

    private final UserFacade userFacade;
    private final GroupFacade groupFacade;
    private final SuperGroupFacade superGroupFacade;

    public ApiV1Controller(UserFacade userFacade, GroupFacade groupFacade, SuperGroupFacade superGroupFacade) {
        this.userFacade = userFacade;
        this.groupFacade = groupFacade;
        this.superGroupFacade = superGroupFacade;
    }

    @GetMapping("/groups")
    public List<Group> getGroups() {
        return this.groupService.getAll();
    }

    @GetMapping("/superGroups")
    public List<SuperGroup> getSuperGroups() {
        return this.superGroupService.getAll();
    }

    @GetMapping("/users")
    public List<User> getUsersForClient() {
//        return this.userService.getAll();
        return null;
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable("id") UserId id) {
//        try {
//            return new User(this.userService.get(id));
//        } catch (UserService.UserNotFoundException e) {
//            throw new UserNotFoundResponse();
//    }
        return null;
    }

    @GetMapping("/users/{id}/avatar")
    public void getUserAvatar(@PathVariable("id") UserId id, HttpServletResponse response) throws IOException { }

    private record GetMeResponse(@JsonUnwrapped User user,
                                 List<GroupPost> groups,
                                 Collection<GrantedAuthorityProxy> authorities) { }

    @GetMapping("/users/me")
    public GetMeResponse getMe() {
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
