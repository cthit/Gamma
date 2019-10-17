package it.chalmers.delta.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class EditedMembershipResponse extends ResponseEntity<String> {
    public EditedMembershipResponse() {
        super("EDITED_MEMBERSHIP", HttpStatus.ACCEPTED);
    }
}
