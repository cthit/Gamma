package it.chalmers.gamma.response;

import it.chalmers.gamma.db.entity.FKITSuperGroup;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class FKITSuperGroupCreatedResponse extends ResponseEntity<FKITSuperGroup> {

    public FKITSuperGroupCreatedResponse(FKITSuperGroup group) {
        super(group, HttpStatus.ACCEPTED);
    }
}