package it.chalmers.gamma.group.controller.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GroupDeletedResponse extends ResponseEntity<String> {
    public GroupDeletedResponse() {
        super("DELETED_GROUP", HttpStatus.ACCEPTED);
    }
}