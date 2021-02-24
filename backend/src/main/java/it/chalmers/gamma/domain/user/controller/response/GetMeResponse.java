package it.chalmers.gamma.domain.user.controller.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.domain.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.domain.membership.data.dto.UserRestrictedWithGroupsDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class GetMeResponse {

    private final UserRestrictedWithGroupsDTO user;
    private final List<AuthorityLevelName> authorities;

    public GetMeResponse(UserRestrictedWithGroupsDTO user, List<AuthorityLevelName> authorities) {
        this.user = user;
        this.authorities = authorities;
    }

    @JsonUnwrapped
    public UserRestrictedWithGroupsDTO getUser() {
        return user;
    }

    public List<AuthorityLevelName> getAuthorities() {
        return authorities;
    }

    public GetMeResponseObject toResponseObject() {
        return new GetMeResponseObject(this);
    }

    public static class GetMeResponseObject extends ResponseEntity<GetMeResponse> {

        public GetMeResponseObject(GetMeResponse body) {
            super(body, HttpStatus.OK);
        }
    }

}
