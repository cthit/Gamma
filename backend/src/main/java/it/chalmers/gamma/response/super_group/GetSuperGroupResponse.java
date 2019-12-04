package it.chalmers.gamma.response.super_group;

import it.chalmers.gamma.domain.dto.group.FKITSuperGroupDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetSuperGroupResponse {
    private final FKITSuperGroupDTO fkitSuperGroup;

    public GetSuperGroupResponse(FKITSuperGroupDTO fkitSuperGroup) {
        this.fkitSuperGroup = fkitSuperGroup;
    }

    public FKITSuperGroupDTO getFkitSuperGroup() {
        return fkitSuperGroup;
    }

    public GetSuperGroupResponseObject getResponseObject() {
        return new GetSuperGroupResponseObject(this);
    }

    public static class GetSuperGroupResponseObject extends ResponseEntity<GetSuperGroupResponse> {
        GetSuperGroupResponseObject(GetSuperGroupResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}

