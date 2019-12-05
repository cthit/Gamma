package it.chalmers.gamma.response.group;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetAllFKITGroupsResponse {
    private final List<GetFKITGroupResponse> responses;

    public GetAllFKITGroupsResponse(List<GetFKITGroupResponse> responses) {
        this.responses = responses;
    }

    public List<GetFKITGroupResponse> getResponses() {
        return responses;
    }

    public static class GetAllFKITGroupsResponseObject extends ResponseEntity<GetAllFKITGroupsResponse> {
        GetAllFKITGroupsResponseObject(GetAllFKITGroupsResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
