package it.chalmers.gamma.response.user;

import it.chalmers.gamma.domain.dto.user.ITUserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetITUserMinifiedResponse {
    private final ITUserDTO user;

    public GetITUserMinifiedResponse(ITUserDTO user) {
        this.user = user;
    }

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
