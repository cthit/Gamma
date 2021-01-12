package it.chalmers.gamma.user.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import it.chalmers.gamma.group.FKITGroupDTO;
import it.chalmers.gamma.user.ITUserRestrictedDTO;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetITUserRestrictedResponse {

    @JsonUnwrapped
    private final ITUserRestrictedDTO user;
    private final List<FKITGroupDTO> groups;

    public GetITUserRestrictedResponse(
            ITUserRestrictedDTO user,
            List<FKITGroupDTO> groups) {
        this.user = user;
        this.groups = groups;
    }

    public ITUserRestrictedDTO getUser() {
        return this.user;
    }

    public List<FKITGroupDTO> getGroups() {
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
