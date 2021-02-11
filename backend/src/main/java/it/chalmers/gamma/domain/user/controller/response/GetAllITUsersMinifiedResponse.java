package it.chalmers.gamma.domain.user.controller.response;

import java.util.List;

import it.chalmers.gamma.domain.user.data.UserRestrictedDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetAllITUsersMinifiedResponse {

    private final List<UserRestrictedDTO> users;

    public GetAllITUsersMinifiedResponse(List<UserRestrictedDTO> users) {
        this.users = users;
    }

    public List<UserRestrictedDTO> getUsers() {
        return users;
    }

    public GetAllITUsersMinifiedResponseObject toResponseObject() {
        return new GetAllITUsersMinifiedResponseObject(this);
    }

    public static class GetAllITUsersMinifiedResponseObject extends ResponseEntity<GetAllITUsersMinifiedResponse> {
        GetAllITUsersMinifiedResponseObject(GetAllITUsersMinifiedResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
