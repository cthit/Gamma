package it.chalmers.gamma.group.controller.response;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetAllFKITGroupsMinifiedResponse {

    private final List<GetFKITGroupMinifiedResponse> groups;

    public GetAllFKITGroupsMinifiedResponse(List<GetFKITGroupMinifiedResponse> groups) {
        this.groups = groups;
    }
    @JsonValue
    public List<GetFKITGroupMinifiedResponse> getGroups() {
        return this.groups;
    }

    public GetAllFKITGroupsMinifiedResponseObject toResponseObject() {
        return new GetAllFKITGroupsMinifiedResponseObject(this);
    }

    public static class GetAllFKITGroupsMinifiedResponseObject
            extends ResponseEntity<GetAllFKITGroupsMinifiedResponse> {
        GetAllFKITGroupsMinifiedResponseObject(GetAllFKITGroupsMinifiedResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
