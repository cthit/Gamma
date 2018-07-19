package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GroupDoesNotExistResponse extends ResponseEntity<String>{
    public GroupDoesNotExistResponse() {
        super("NO_SUCH_GROUP_EXISTS", HttpStatus.NOT_FOUND);
    }
}
