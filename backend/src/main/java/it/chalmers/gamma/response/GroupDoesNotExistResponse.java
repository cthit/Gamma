package it.chalmers.delta.response;

import org.springframework.http.HttpStatus;

public class GroupDoesNotExistResponse extends CustomResponseStatusException {
    public GroupDoesNotExistResponse() {
        super(HttpStatus.NOT_FOUND, "NO_SUCH_GROUP_EXISTS");
    }
}
