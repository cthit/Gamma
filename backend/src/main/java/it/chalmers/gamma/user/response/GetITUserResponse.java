package it.chalmers.gamma.user.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import it.chalmers.gamma.domain.group.FKITGroupDTO;
import it.chalmers.gamma.domain.user.ITUserDTO;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetITUserResponse {

    @JsonUnwrapped
    private final ITUserDTO user;
    private final List<FKITGroupDTO> groups;

    public GetITUserResponse(ITUserDTO user,
                             List<FKITGroupDTO> groups) {
        this.user = user;
        this.groups = groups;
    }

    public GetITUserResponse(ITUserDTO user) {
        this(user, null);
    }

    @JsonUnwrapped
    public ITUserDTO getUser() {
        return this.user;
    }

    public List<FKITGroupDTO> getGroups() {
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
