package it.chalmers.gamma.domain.group.controller.response;

import java.util.List;

import it.chalmers.gamma.domain.group.data.GroupMinifiedDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetAllGroupsMinifiedResponse {

    private final List<GroupMinifiedDTO> groups;

    public GetAllGroupsMinifiedResponse(List<GroupMinifiedDTO> groups) {
        this.groups = groups;
    }

    public List<GroupMinifiedDTO> getGroups() {
        return groups;
    }

    public GetAllGroupsMinifiedResponseObject toResponseObject() {
        return new GetAllGroupsMinifiedResponseObject(this);
    }

    public static class GetAllGroupsMinifiedResponseObject extends ResponseEntity<GetAllGroupsMinifiedResponse> {
        GetAllGroupsMinifiedResponseObject(GetAllGroupsMinifiedResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
