package it.chalmers.gamma.group.controller.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.group.dto.GroupMinifiedDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetActiveGroupsResponse {

    @JsonUnwrapped
    private final List<GetGroupResponse> groups;

    public GetActiveGroupsResponse(List<GetGroupResponse> groups) {
        this.groups = groups;
    }

    public List<GetGroupResponse> getGroups() {
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
