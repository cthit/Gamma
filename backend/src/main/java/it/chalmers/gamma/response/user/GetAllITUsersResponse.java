package it.chalmers.gamma.response.user;

import it.chalmers.gamma.domain.dto.user.ITUserDTO;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetAllITUsersResponse {
    private final List<GetITUserResponse> users;

    public GetAllITUsersResponse(List<GetITUserResponse> users) {
        this.users = users;
    }

    public List<GetITUserResponse> getUsers() {
        return users;
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
