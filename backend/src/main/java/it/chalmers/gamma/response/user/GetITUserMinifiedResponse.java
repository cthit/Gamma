package it.chalmers.gamma.response.user;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import it.chalmers.gamma.domain.dto.user.ITUserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetITUserMinifiedResponse {

    @JsonUnwrapped
    private final ITUserDTO user;

    public GetITUserMinifiedResponse(ITUserDTO user) {
        this.user = user;
    }

    @JsonUnwrapped
    public ITUserDTO getUser() {
        return user;
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
