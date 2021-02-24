package it.chalmers.gamma.domain.user.controller.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import it.chalmers.gamma.domain.membership.data.dto.UserRestrictedWithGroupsDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetUserRestrictedResponse {

    @JsonUnwrapped
    private final UserRestrictedWithGroupsDTO user;

    public GetUserRestrictedResponse(UserRestrictedWithGroupsDTO user) {
        this.user = user;
    }

    public UserRestrictedWithGroupsDTO getUser() {
        return user;
    }

    @JsonIgnore
    public GetITUserRestrictedResponseObject toResponseObject() {
        return new GetITUserRestrictedResponseObject(this);
    }

    public static class GetITUserRestrictedResponseObject extends ResponseEntity<GetUserRestrictedResponse> {
        GetITUserRestrictedResponseObject(GetUserRestrictedResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
