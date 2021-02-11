package it.chalmers.gamma.domain.supergroup.controller.response;

import it.chalmers.gamma.domain.supergroup.data.SuperGroup;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class SuperGroupCreatedResponse extends ResponseEntity<SuperGroup> {

    public SuperGroupCreatedResponse(SuperGroup group) {
        super(group, HttpStatus.ACCEPTED);
    }
}