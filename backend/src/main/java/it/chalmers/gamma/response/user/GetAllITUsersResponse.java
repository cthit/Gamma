package it.chalmers.gamma.response.user;

import it.chalmers.gamma.domain.dto.user.ITUserDTO;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetAllITUsersResponse {
    private final List<ITUserDTO> users;

    public GetAllITUsersResponse(List<ITUserDTO> users) {
        this.users = users;
    }

    public List<ITUserDTO> getUsers() {
        return users;
    }

    public GetAllITUsersResponseObject getResponseObject() {
        return new GetAllITUsersResponseObject(this);
    }

    public static class GetAllITUsersResponseObject extends ResponseEntity<GetAllITUsersResponse> {
        GetAllITUsersResponseObject(GetAllITUsersResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
