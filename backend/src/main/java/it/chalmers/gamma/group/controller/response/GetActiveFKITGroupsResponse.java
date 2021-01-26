package it.chalmers.gamma.group.controller.response;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetActiveFKITGroupsResponse {
    private final List<GetFKITGroupResponse> getFKITGroupResponse;

    public GetActiveFKITGroupsResponse(List<GetFKITGroupResponse> getFKITGroupResponse) {
        this.getFKITGroupResponse = getFKITGroupResponse;
    }

    public List<GetFKITGroupResponse> getGetFKITGroupResponse() {
        return this.getFKITGroupResponse;
    }

    public GetActiveFKITGroupResponseObject toResponseObject() {
        return new GetActiveFKITGroupResponseObject(this);
    }

    public static class GetActiveFKITGroupResponseObject extends ResponseEntity<GetActiveFKITGroupsResponse> {
        GetActiveFKITGroupResponseObject(GetActiveFKITGroupsResponse body) {
            super(body, HttpStatus.OK);
        }
    }

}
