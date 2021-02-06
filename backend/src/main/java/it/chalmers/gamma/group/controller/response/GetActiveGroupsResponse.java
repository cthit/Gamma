package it.chalmers.gamma.group.controller.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.group.dto.GroupMinifiedDTO;
import it.chalmers.gamma.group.dto.GroupWithMembersDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetActiveGroupsResponse {

    @JsonUnwrapped
    private final List<GroupWithMembersDTO> groups;

    public GetActiveGroupsResponse(List<GroupWithMembersDTO> groups) {
        this.groups = groups;
    }

    public List<GroupWithMembersDTO> getGroups() {
        return groups;
    }

    public GetActiveGroupResponseObject toResponseObject() {
        return new GetActiveGroupResponseObject(this);
    }

    public static class GetActiveGroupResponseObject extends ResponseEntity<GetActiveGroupsResponse> {
        GetActiveGroupResponseObject(GetActiveGroupsResponse body) {
            super(body, HttpStatus.OK);
        }
    }

}
