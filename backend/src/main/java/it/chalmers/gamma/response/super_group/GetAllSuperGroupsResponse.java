package it.chalmers.gamma.response.super_group;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetAllSuperGroupsResponse {
    private final List<GetSuperGroupResponse> responses;

    public GetAllSuperGroupsResponse(List<GetSuperGroupResponse> responses) {
        this.responses = responses;
    }

    public List<GetSuperGroupResponse> getResponses() {
        return responses;
    }

    public GetAllSuperGroupsResponseObject getResponseObject() {
        return new GetAllSuperGroupsResponseObject(this);
    }

    public static class GetAllSuperGroupsResponseObject extends ResponseEntity<GetAllSuperGroupsResponse> {
        GetAllSuperGroupsResponseObject(GetAllSuperGroupsResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
