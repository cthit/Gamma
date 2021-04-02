package it.chalmers.gamma.domain.supergroup.controller.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class SuperGroupCreatedResponse extends ResponseEntity<String> {

    public SuperGroupCreatedResponse() {
        super("SUPER_GROUP_CREATED", HttpStatus.ACCEPTED);
    }
}