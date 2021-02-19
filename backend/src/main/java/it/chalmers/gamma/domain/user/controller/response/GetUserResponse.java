package it.chalmers.gamma.domain.user.controller.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import it.chalmers.gamma.domain.membership.data.UserRestrictedWithGroupsDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetUserResponse {

    @JsonUnwrapped
    private final UserRestrictedWithGroupsDTO user;

    public GetUserResponse(UserRestrictedWithGroupsDTO user) {
        this.user = user;
    }

    public GetUserResponseObject toResponseObject() {
        return new GetUserResponseObject(this);
    }

    public static class GetUserResponseObject extends ResponseEntity<GetUserResponse> {
        GetUserResponseObject(GetUserResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
