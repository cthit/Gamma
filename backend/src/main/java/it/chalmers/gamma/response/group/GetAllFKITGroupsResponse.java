package it.chalmers.gamma.response.group;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetAllFKITGroupsResponse {
    @JsonUnwrapped
    private final List<GetFKITGroupResponse> groups;

    public GetAllFKITGroupsResponse(List<GetFKITGroupResponse> groups) {
        this.groups = groups;
    }

    public List<GetFKITGroupResponse> getGroups() {
        return groups;
    }

    public GetAllFKITGroupsResponseObject toResponseObject() {
        return new GetAllFKITGroupsResponseObject(this);
    }

    public static class GetAllFKITGroupsResponseObject extends ResponseEntity<GetAllFKITGroupsResponse> {
        GetAllFKITGroupsResponseObject(GetAllFKITGroupsResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
