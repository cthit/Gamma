package it.chalmers.gamma.supergroup.response;

import it.chalmers.gamma.supergroup.FKITSuperGroup;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class FKITSuperGroupCreatedResponse extends ResponseEntity<FKITSuperGroup> {

    public FKITSuperGroupCreatedResponse(FKITSuperGroup group) {
        super(group, HttpStatus.ACCEPTED);
    }
}