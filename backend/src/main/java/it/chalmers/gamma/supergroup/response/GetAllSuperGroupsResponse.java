package it.chalmers.gamma.supergroup.response;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetAllSuperGroupsResponse {
    @JsonValue
    private final List<GetSuperGroupResponse> superGroups;

    public GetAllSuperGroupsResponse(List<GetSuperGroupResponse> superGroups) {
        this.superGroups = superGroups;
    }

    public List<GetSuperGroupResponse> getSuperGroups() {
        return this.superGroups;
    }

    public GetAllSuperGroupsResponseObject toResponseObject() {
        return new GetAllSuperGroupsResponseObject(this);
    }

    public static class GetAllSuperGroupsResponseObject extends ResponseEntity<GetAllSuperGroupsResponse> {
        GetAllSuperGroupsResponseObject(GetAllSuperGroupsResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
