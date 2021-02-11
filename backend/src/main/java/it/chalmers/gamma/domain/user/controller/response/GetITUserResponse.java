package it.chalmers.gamma.domain.user.controller.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import it.chalmers.gamma.domain.user.data.UserRestrictedWithGroupsDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetITUserResponse {

    @JsonUnwrapped
    private final UserRestrictedWithGroupsDTO user;

    public GetITUserResponse(UserRestrictedWithGroupsDTO user) {
        this.user = user;
    }

    public UserRestrictedWithGroupsDTO getUser() {
        return user;
    }

    @JsonIgnore
    public GetITUserResponseObject toResponseObject() {
        return new GetITUserResponseObject(this);
    }

    public static class GetITUserResponseObject extends ResponseEntity<GetITUserResponse> {
        GetITUserResponseObject(GetITUserResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
