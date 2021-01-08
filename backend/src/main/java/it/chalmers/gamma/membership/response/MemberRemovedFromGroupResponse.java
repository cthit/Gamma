package it.chalmers.gamma.membership.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class MemberRemovedFromGroupResponse extends ResponseEntity<String> {

    public MemberRemovedFromGroupResponse() {
        super("USER_REMOVED_FROM_GROUP", HttpStatus.OK);
    }
}
