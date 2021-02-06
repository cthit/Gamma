package it.chalmers.gamma.supergroup.response;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;

import it.chalmers.gamma.supergroup.SuperGroupDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetAllSuperGroupsResponse {
    @JsonValue
    private final List<SuperGroupDTO> superGroups;

    public GetAllSuperGroupsResponse(List<SuperGroupDTO> superGroups) {
        this.superGroups = superGroups;
    }

    public List<SuperGroupDTO> getSuperGroups() {
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
