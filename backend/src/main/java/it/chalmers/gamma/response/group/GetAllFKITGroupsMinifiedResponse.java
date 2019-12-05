package it.chalmers.gamma.response.group;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetAllFKITGroupsMinifiedResponse {
    private final List<GetFKITGroupMinifiedResponse> groupResponse;

    public GetAllFKITGroupsMinifiedResponse(List<GetFKITGroupMinifiedResponse> groupResponse) {
        this.groupResponse = groupResponse;
    }

    public List<GetFKITGroupMinifiedResponse> getGroupResponse() {
        return groupResponse;
    }

    public GetAllFKITGroupsMinifiedResponseObject toResponseObject() {
        return new GetAllFKITGroupsMinifiedResponseObject(this);
    }

    public static class GetAllFKITGroupsMinifiedResponseObject extends ResponseEntity<GetAllFKITGroupsMinifiedResponse> {
        GetAllFKITGroupsMinifiedResponseObject(GetAllFKITGroupsMinifiedResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
