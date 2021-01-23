package it.chalmers.gamma.user.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import it.chalmers.gamma.user.UserRestrictedDTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetITUserMinifiedResponse {

    @JsonUnwrapped
    private final UserRestrictedDTO user;

    public GetITUserMinifiedResponse(UserRestrictedDTO user) {
        this.user = user;
    }

    @JsonUnwrapped
    public UserRestrictedDTO getUser() {
        return this.user;
    }

    public GetITUserMinifiedResponseObject toResponseObject() {
        return new GetITUserMinifiedResponseObject(this);
    }

    public static class GetITUserMinifiedResponseObject extends ResponseEntity<GetITUserMinifiedResponse> {
        GetITUserMinifiedResponseObject(GetITUserMinifiedResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
