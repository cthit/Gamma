package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class GroupDoesNotExistResponse extends ResponseStatusException {
    public GroupDoesNotExistResponse() {
        super(HttpStatus.NOT_FOUND, "NO_SUCH_GROUP_EXISTS");
    }
}
