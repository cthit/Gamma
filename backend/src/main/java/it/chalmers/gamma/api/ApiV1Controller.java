package it.chalmers.gamma.api;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.internal.authority.level.service.AuthorityLevelName;
import it.chalmers.gamma.internal.authority.service.AuthorityFinder;
import it.chalmers.gamma.domain.Group;
import it.chalmers.gamma.internal.group.service.GroupService;
import it.chalmers.gamma.internal.membership.service.MembershipService;
import it.chalmers.gamma.internal.supergroup.service.SuperGroupDTO;
import it.chalmers.gamma.internal.supergroup.service.SuperGroupService;
import it.chalmers.gamma.internal.user.service.UserDTO;
import it.chalmers.gamma.domain.UserId;
import it.chalmers.gamma.internal.user.service.UserRestrictedDTO;
import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.GroupPost;
import it.chalmers.gamma.internal.user.service.UserService;
import it.chalmers.gamma.util.response.ErrorResponse;
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
    public List<SuperGroupDTO> getSuperGroups() {
        return this.superGroupService.getAll();
    }

    @GetMapping("/users")
    public List<UserRestrictedDTO> getUsersForClient() {
        return this.userService.getAll();
    }

    @GetMapping("/users/{id}")
    public UserRestrictedDTO getUser(@PathVariable("id") UserId id) {
        try {
            return new UserRestrictedDTO(this.userService.get(id));
        } catch (UserService.UserNotFoundException e) {
            throw new UserNotFoundResponse();
        }
    }

    @GetMapping("/users/{id}/avatar")
    public void getUserAvatar(@PathVariable("id") UserId id, HttpServletResponse response) throws IOException {

    }

    private record GetMeResponse(@JsonUnwrapped UserRestrictedDTO user,
                                List<GroupPost> groups,
                                List<AuthorityLevelName> authorities) { }

    @GetMapping("/users/me")
    public GetMeResponse getMe(Principal principal) {
        try {
            UserDTO user = this.userService.get(new Cid(principal.getName()));
            List<GroupPost> groups = this.membershipService.getMembershipsByUser(user.id())
                    .stream()
                    .map(membership -> new GroupPost(membership.post(), membership.group()))
                    .collect(Collectors.toList());
            List<AuthorityLevelName> authorityLevelNames = this.authorityFinder.getGrantedAuthorities(user.id());

            return new GetMeResponse(new UserRestrictedDTO(user), groups, authorityLevelNames);
        } catch (UserService.UserNotFoundException e) {
            throw new UserNotFoundResponse();
        }
    }

    private static class UserNotFoundResponse extends ErrorResponse {
        private UserNotFoundResponse() {
            super(HttpStatus.NOT_FOUND);
        }
    }

}
