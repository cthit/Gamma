package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class UserAddedToGroupResponse extends ResponseEntity<String> {
    public UserAddedToGroupResponse() {
        super("USER_WAS_ADDED_TO_GROUP", HttpStatus.ACCEPTED);
    }
}
