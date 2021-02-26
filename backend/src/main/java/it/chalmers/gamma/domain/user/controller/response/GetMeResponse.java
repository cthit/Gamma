package it.chalmers.gamma.domain.user.controller.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.domain.GroupPost;
import it.chalmers.gamma.domain.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.domain.user.data.UserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class GetMeResponse {

    public final UserDTO user;
    public final List<GroupPost> groups;
    public final List<AuthorityLevelName> authorities;

    public GetMeResponse(UserDTO user, List<GroupPost> groups, List<AuthorityLevelName> authorities) {
        this.user = user;
        this.groups = groups;
        this.authorities = authorities;
    }
}
