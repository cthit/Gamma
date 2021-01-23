package it.chalmers.gamma.supergroup.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.supergroup.SuperGroupDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetSuperGroupResponse {
    @JsonUnwrapped
    private final SuperGroupDTO fkitSuperGroup;

    public GetSuperGroupResponse(SuperGroupDTO fkitSuperGroup) {
        this.fkitSuperGroup = fkitSuperGroup;
    }

    public SuperGroupDTO getFkitSuperGroup() {
        return this.fkitSuperGroup;
    }

    public GetSuperGroupResponseObject toResponseObject() {
        return new GetSuperGroupResponseObject(this);
    }

    public static class GetSuperGroupResponseObject extends ResponseEntity<GetSuperGroupResponse> {
        GetSuperGroupResponseObject(GetSuperGroupResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}

