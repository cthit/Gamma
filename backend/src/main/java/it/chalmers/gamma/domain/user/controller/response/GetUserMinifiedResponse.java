package it.chalmers.gamma.domain.user.controller.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import it.chalmers.gamma.domain.user.data.UserRestrictedDTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetUserMinifiedResponse {

    @JsonUnwrapped
    private final UserRestrictedDTO user;

    public GetUserMinifiedResponse(UserRestrictedDTO user) {
        this.user = user;
    }

    @JsonUnwrapped
    public UserRestrictedDTO getUser() {
        return this.user;
    }

    public GetITUserMinifiedResponseObject toResponseObject() {
        return new GetITUserMinifiedResponseObject(this);
    }

    public static class GetITUserMinifiedResponseObject extends ResponseEntity<GetUserMinifiedResponse> {
        GetITUserMinifiedResponseObject(GetUserMinifiedResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
