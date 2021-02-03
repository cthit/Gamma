package it.chalmers.gamma.group.controller.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.group.dto.GroupMinifiedDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetGroupMinifiedResponse {

    @JsonUnwrapped
    private final GroupMinifiedDTO group;

    public GetGroupMinifiedResponse(GroupMinifiedDTO group) {
        this.group = group;
    }

    public GroupMinifiedDTO getGroup() {
        return this.group;
    }

    public GetGroupMinifiedResponseObject toResponseObject() {
        return new GetGroupMinifiedResponseObject(this);
    }

    public static class GetGroupMinifiedResponseObject extends ResponseEntity<GetGroupMinifiedResponse> {
        GetGroupMinifiedResponseObject(GetGroupMinifiedResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
