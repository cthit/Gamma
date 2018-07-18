package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GroupAlreadyExistsResponse extends ResponseEntity<String>{
    public GroupAlreadyExistsResponse() {
        super("GROUP_ALREADY_EXISTS", HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
