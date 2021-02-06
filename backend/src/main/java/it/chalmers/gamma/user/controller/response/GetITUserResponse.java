package it.chalmers.gamma.user.controller.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import it.chalmers.gamma.group.dto.GroupDTO;
import it.chalmers.gamma.user.dto.UserDTO;
import java.util.List;

import it.chalmers.gamma.user.dto.UserWithMembershipsDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetITUserResponse {

    @JsonUnwrapped
    private final UserWithMembershipsDTO user;

    public GetITUserResponse(UserWithMembershipsDTO user) {
        this.user = user;
    }

    public UserWithMembershipsDTO getUser() {
        return user;
    }

    @JsonIgnore
    public GetITUserResponseObject toResponseObject() {
        return new GetITUserResponseObject(this);
    }

    public static class GetITUserResponseObject extends ResponseEntity<GetITUserResponse> {
        GetITUserResponseObject(GetITUserResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
