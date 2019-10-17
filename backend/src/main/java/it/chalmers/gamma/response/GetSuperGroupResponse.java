package it.chalmers.delta.response;

import it.chalmers.delta.db.entity.FKITSuperGroup;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetSuperGroupResponse extends ResponseEntity<FKITSuperGroup> {
    public GetSuperGroupResponse(FKITSuperGroup superGroup) {
        super(superGroup, HttpStatus.ACCEPTED);
    }
}
