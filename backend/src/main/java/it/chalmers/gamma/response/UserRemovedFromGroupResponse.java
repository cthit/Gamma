package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class UserRemovedFromGroupResponse extends ResponseEntity<String> {

    public UserRemovedFromGroupResponse() {
        super("USER_REMOVED_FROM_GROUP", HttpStatus.OK);
    }
}
