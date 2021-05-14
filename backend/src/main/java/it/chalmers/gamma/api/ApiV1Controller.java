package it.chalmers.gamma.api;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.internal.authority.service.post.AuthorityPostFinder;
import it.chalmers.gamma.internal.authoritylevel.service.AuthorityLevelName;
import it.chalmers.gamma.internal.group.service.GroupDTO;
import it.chalmers.gamma.internal.group.service.GroupFinder;
import it.chalmers.gamma.internal.membership.service.MembershipFinder;
import it.chalmers.gamma.internal.supergroup.service.SuperGroupDTO;
import it.chalmers.gamma.internal.supergroup.service.SuperGroupFinder;
import it.chalmers.gamma.internal.user.service.UserDTO;
import it.chalmers.gamma.internal.user.service.UserFinder;
import it.chalmers.gamma.internal.user.service.UserId;
import it.chalmers.gamma.internal.user.service.UserRestrictedDTO;
import it.chalmers.gamma.util.domain.Cid;
import it.chalmers.gamma.util.domain.GroupPost;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
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
@RequestMapping("/v1")
public class ApiV1Controller {

    private final UserFinder userFinder;
    private final MembershipFinder membershipFinder;
    private final AuthorityPostFinder authorityPostFinder;
    private final GroupFinder groupFinder;
    private final SuperGroupFinder superGroupFinder;

    public ApiV1Controller(UserFinder userFinder,
                           MembershipFinder membershipFinder,
                           AuthorityPostFinder authorityPostFinder,
                           GroupFinder groupFinder,
                           SuperGroupFinder superGroupFinder) {
        this.userFinder = userFinder;
        this.membershipFinder = membershipFinder;
        this.authorityPostFinder = authorityPostFinder;
        this.groupFinder = groupFinder;
        this.superGroupFinder = superGroupFinder;
    }

    @GetMapping("/groups")
    public List<GroupDTO> getGroups() {
        return this.groupFinder.getAll();
    }

    @GetMapping("/superGroups")
    public List<SuperGroupDTO> getSuperGroups() {
        return this.superGroupFinder.getAll();
    }

    @GetMapping("/users")
    public List<UserRestrictedDTO> getUsersForClient() {
        return this.userFinder.getAll();
    }

    @GetMapping("/users/{id}")
    public UserRestrictedDTO getUser(@PathVariable("id") UserId id) {
        try {
            return new UserRestrictedDTO(this.userFinder.get(id));
        } catch (EntityNotFoundException e) {
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
            UserDTO user = this.userFinder.get(new Cid(principal.getName()));
            List<GroupPost> groups = this.membershipFinder.getMembershipsByUser(user.id())
                    .stream()
                    .map(membership -> new GroupPost(membership.post(), membership.group()))
                    .collect(Collectors.toList());
            List<AuthorityLevelName> authorityLevelNames = this.authorityPostFinder.getGrantedAuthorities(user.id());

            return new GetMeResponse(new UserRestrictedDTO(user), groups, authorityLevelNames);
        } catch (EntityNotFoundException e) {
            throw new UserNotFoundResponse();
        }
    }

    private static class UserNotFoundResponse extends ErrorResponse {
        private UserNotFoundResponse() {
            super(HttpStatus.NOT_FOUND);
        }
    }

}
