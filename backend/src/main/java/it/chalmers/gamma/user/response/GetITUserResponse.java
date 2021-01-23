package it.chalmers.gamma.user.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import it.chalmers.gamma.group.GroupDTO;
import it.chalmers.gamma.user.UserDTO;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetITUserResponse {

    @JsonUnwrapped
    private final UserDTO user;
    private final List<GroupDTO> groups;

    public GetITUserResponse(UserDTO user,
                             List<GroupDTO> groups) {
        this.user = user;
        this.groups = groups;
    }

    public GetITUserResponse(UserDTO user) {
        this(user, null);
    }

    @JsonUnwrapped
    public UserDTO getUser() {
        return this.user;
    }

    public List<GroupDTO> getGroups() {
        return this.groups;
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
