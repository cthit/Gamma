package it.chalmers.delta.response;

import it.chalmers.delta.db.entity.FKITSuperGroup;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class FKITSuperGroupCreatedResponse extends ResponseEntity<FKITSuperGroup> {

    public FKITSuperGroupCreatedResponse(FKITSuperGroup group) {
        super(group, HttpStatus.ACCEPTED);
    }
}