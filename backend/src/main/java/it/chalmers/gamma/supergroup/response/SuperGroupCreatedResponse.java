package it.chalmers.gamma.supergroup.response;

import it.chalmers.gamma.supergroup.SuperGroup;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class SuperGroupCreatedResponse extends ResponseEntity<SuperGroup> {

    public SuperGroupCreatedResponse(SuperGroup group) {
        super(group, HttpStatus.ACCEPTED);
    }
}