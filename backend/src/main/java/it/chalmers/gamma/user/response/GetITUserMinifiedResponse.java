package it.chalmers.gamma.user.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import it.chalmers.gamma.domain.user.ITUserRestrictedDTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetITUserMinifiedResponse {

    @JsonUnwrapped
    private final ITUserRestrictedDTO user;

    public GetITUserMinifiedResponse(ITUserRestrictedDTO user) {
        this.user = user;
    }

    @JsonUnwrapped
    public ITUserRestrictedDTO getUser() {
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
