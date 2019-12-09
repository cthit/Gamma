package it.chalmers.gamma.response.user;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetAllITUsersMinifiedResponse {

    private final List<GetITUserMinifiedResponse> users;

    public GetAllITUsersMinifiedResponse(List<GetITUserMinifiedResponse> users) {
        this.users = users;
    }

    public List<GetITUserMinifiedResponse> getUsers() {
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
