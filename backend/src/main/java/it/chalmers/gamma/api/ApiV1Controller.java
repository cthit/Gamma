package it.chalmers.gamma.api;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.domain.Authority;
import it.chalmers.gamma.domain.AuthorityLevelName;
import it.chalmers.gamma.internal.authority.service.AuthorityFinder;
import it.chalmers.gamma.domain.Group;
import it.chalmers.gamma.internal.group.service.GroupService;
import it.chalmers.gamma.internal.membership.service.MembershipService;
import it.chalmers.gamma.domain.SuperGroup;
import it.chalmers.gamma.internal.supergroup.service.SuperGroupService;
import it.chalmers.gamma.domain.User;
import it.chalmers.gamma.domain.UserId;
import it.chalmers.gamma.domain.UserRestricted;
import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.GroupPost;
import it.chalmers.gamma.internal.user.service.UserService;
import it.chalmers.gamma.util.response.ErrorResponse;
import it.chalmers.gamma.util.response.NotFoundResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The API must not be altered since it's used by a lot of different clients.
 * If you need changes, then create a new version of the API.
 */
@RestController
@RequestMapping(ApiV1Controller.URI)
public class ApiV1Controller {

    public static final String URI = "/v1";

    private final UserService userService;
    private final MembershipService membershipService;
    private final AuthorityFinder authorityFinder;
    private final GroupService groupService;
    private final SuperGroupService superGroupService;

    public ApiV1Controller(UserService userService,
                           MembershipService membershipService,
                           AuthorityFinder authorityFinder,
                           GroupService groupService,
                           SuperGroupService superGroupService) {
        this.userService = userService;
        this.membershipService = membershipService;
        this.authorityFinder = authorityFinder;
        this.groupService = groupService;
        this.superGroupService = superGroupService;
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
    public List<UserRestricted> getUsersForClient() {
        return this.userService.getAll();
    }

    @GetMapping("/users/{id}")
    public UserRestricted getUser(@PathVariable("id") UserId id) {
        try {
            return new UserRestricted(this.userService.get(id));
        } catch (UserService.UserNotFoundException e) {
            throw new UserNotFoundResponse();
        }
    }

    @GetMapping("/users/{id}/avatar")
    public void getUserAvatar(@PathVariable("id") UserId id, HttpServletResponse response) throws IOException {

    }

    private record GetMeResponse(@JsonUnwrapped UserRestricted user,
                                List<GroupPost> groups,
                                List<Authority> authorities) { }

    @GetMapping("/users/me")
    public GetMeResponse getMe(Principal principal) {
        try {
            User user = this.userService.get(Cid.valueOf(principal.getName()));
            List<GroupPost> groups = this.membershipService.getMembershipsByUser(user.id())
                    .stream()
                    .map(membership -> new GroupPost(membership.post(), membership.group()))
                    .collect(Collectors.toList());
            List<Authority> authorities = this.authorityFinder.getGrantedAuthoritiesWithType(user.id());

            return new GetMeResponse(new UserRestricted(user), groups, authorities);
        } catch (UserService.UserNotFoundException e) {
            throw new UserNotFoundResponse();
        }
    }

    private static class UserNotFoundResponse extends NotFoundResponse { }

}
