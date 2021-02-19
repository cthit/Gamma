package it.chalmers.gamma.domain.user.controller.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.domain.user.data.UserRestrictedDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetAllUsersMinifiedResponse {

    @JsonValue
    private final List<UserRestrictedDTO> users;

    public GetAllUsersMinifiedResponse(List<UserRestrictedDTO> users) {
        this.users = users;
    }

    public GetAllITUsersMinifiedResponseObject toResponseObject() {
        return new GetAllITUsersMinifiedResponseObject(this);
    }

    public static class GetAllITUsersMinifiedResponseObject extends ResponseEntity<GetAllUsersMinifiedResponse> {
        GetAllITUsersMinifiedResponseObject(GetAllUsersMinifiedResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
