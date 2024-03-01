package it.chalmers.gamma.response.supergroup;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.domain.dto.group.FKITSuperGroupDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetSuperGroupResponse {
    @JsonUnwrapped
    private final FKITSuperGroupDTO fkitSuperGroup;

    public GetSuperGroupResponse(FKITSuperGroupDTO fkitSuperGroup) {
        this.fkitSuperGroup = fkitSuperGroup;
    }

    public FKITSuperGroupDTO getFkitSuperGroup() {
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

