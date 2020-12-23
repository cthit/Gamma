package it.chalmers.gamma.user.response;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetAllITUsersResponse {
    @JsonValue
    private final List<GetITUserResponse> users;

    public GetAllITUsersResponse(List<GetITUserResponse> users) {
        this.users = users;
    }

    public List<GetITUserResponse> getUsers() {
        return this.users;
    }

    public GetAllITUsersResponseObject toResponseObject() {
        return new GetAllITUsersResponseObject(this);
    }

    public static class GetAllITUsersResponseObject extends ResponseEntity<GetAllITUsersResponse> {
        GetAllITUsersResponseObject(GetAllITUsersResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
