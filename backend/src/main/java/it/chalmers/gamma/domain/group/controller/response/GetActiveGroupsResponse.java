package it.chalmers.gamma.domain.group.controller.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.domain.membership.data.MembershipsPerGroupDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetActiveGroupsResponse {

    @JsonUnwrapped
    private final List<MembershipsPerGroupDTO> groups;

    public GetActiveGroupsResponse(List<MembershipsPerGroupDTO> groups) {
        this.groups = groups;
    }

    public List<MembershipsPerGroupDTO> getGroups() {
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
