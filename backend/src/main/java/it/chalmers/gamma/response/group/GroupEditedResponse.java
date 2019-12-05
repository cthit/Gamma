package it.chalmers.gamma.response.group;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GroupEditedResponse extends ResponseEntity<String> {
    public GroupEditedResponse() {
        super("GROUP_EDITED", HttpStatus.ACCEPTED);
    }
}
