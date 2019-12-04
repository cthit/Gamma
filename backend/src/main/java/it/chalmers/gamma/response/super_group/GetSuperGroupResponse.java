package it.chalmers.gamma.response.super_group;

import it.chalmers.gamma.db.entity.FKITSuperGroup;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetSuperGroupResponse extends ResponseEntity<FKITSuperGroup> {
    public GetSuperGroupResponse(FKITSuperGroup superGroup) {
        super(superGroup, HttpStatus.ACCEPTED);
    }
}
