package it.chalmers.gamma.group.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.group.GroupMinifiedDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetFKITGroupMinifiedResponse {
    @JsonUnwrapped
    private final GroupMinifiedDTO group;

    public GetFKITGroupMinifiedResponse(GroupMinifiedDTO group) {
        this.group = group;
    }

    public GroupMinifiedDTO getGroup() {
        return this.group;
    }

    public GetFKITGroupMinifiedResponseObject toResponseObject() {
        return new GetFKITGroupMinifiedResponseObject(this);
    }

    public static class GetFKITGroupMinifiedResponseObject extends ResponseEntity<GetFKITGroupMinifiedResponse> {
        GetFKITGroupMinifiedResponseObject(GetFKITGroupMinifiedResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
