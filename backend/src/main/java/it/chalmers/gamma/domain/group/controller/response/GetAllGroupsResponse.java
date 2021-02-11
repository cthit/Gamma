package it.chalmers.gamma.domain.group.controller.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetAllGroupsResponse {

    @JsonUnwrapped
    private final List<GetGroupResponse> groups;

    public GetAllGroupsResponse(List<GetGroupResponse> groups) {
        this.groups = groups;
    }

    public List<GetGroupResponse> getGroups() {
        return this.groups;
    }

    public GetAllGroupsResponseObject toResponseObject() {
        return new GetAllGroupsResponseObject(this);
    }

    public static class GetAllGroupsResponseObject extends ResponseEntity<GetAllGroupsResponse> {
        GetAllGroupsResponseObject(GetAllGroupsResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
