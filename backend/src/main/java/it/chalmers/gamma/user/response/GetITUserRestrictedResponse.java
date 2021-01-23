package it.chalmers.gamma.user.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import it.chalmers.gamma.group.GroupDTO;
import it.chalmers.gamma.user.UserRestrictedDTO;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetITUserRestrictedResponse {

    @JsonUnwrapped
    private final UserRestrictedDTO user;
    private final List<GroupDTO> groups;

    public GetITUserRestrictedResponse(
            UserRestrictedDTO user,
            List<GroupDTO> groups) {
        this.user = user;
        this.groups = groups;
    }

    public UserRestrictedDTO getUser() {
        return this.user;
    }

    public List<GroupDTO> getGroups() {
        return this.groups;
    }

    @JsonIgnore
    public GetITUserRestrictedResponseObject toResponseObject() {
        return new GetITUserRestrictedResponseObject(this);
    }

    public static class GetITUserRestrictedResponseObject extends ResponseEntity<GetITUserRestrictedResponse> {
        GetITUserRestrictedResponseObject(GetITUserRestrictedResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
