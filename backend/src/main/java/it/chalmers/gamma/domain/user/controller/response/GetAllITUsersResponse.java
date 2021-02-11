package it.chalmers.gamma.domain.user.controller.response;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;

import it.chalmers.gamma.domain.user.data.UserRestrictedWithGroupsDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetAllITUsersResponse {
    @JsonValue
    private final List<UserRestrictedWithGroupsDTO> users;

    public GetAllITUsersResponse(List<UserRestrictedWithGroupsDTO> users) {
        this.users = users;
    }

    public List<UserRestrictedWithGroupsDTO> getUsers() {
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
