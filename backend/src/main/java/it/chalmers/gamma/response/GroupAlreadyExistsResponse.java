package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class GroupAlreadyExistsResponse extends ResponseStatusException {
    public GroupAlreadyExistsResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "GROUP_ALREADY_EXISTS");
    }
}
