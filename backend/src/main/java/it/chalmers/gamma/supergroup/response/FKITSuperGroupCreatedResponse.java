package it.chalmers.gamma.supergroup.response;

import it.chalmers.gamma.supergroup.SuperGroup;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class FKITSuperGroupCreatedResponse extends ResponseEntity<SuperGroup> {

    public FKITSuperGroupCreatedResponse(SuperGroup group) {
        super(group, HttpStatus.ACCEPTED);
    }
}