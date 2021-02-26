package it.chalmers.gamma.domain.membership.controller.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class MemberAddedToGroupResponse extends ResponseEntity<String> {
    public MemberAddedToGroupResponse() {
        super("USER_WAS_ADDED_TO_GROUP", HttpStatus.ACCEPTED);
    }
}