package it.chalmers.gamma.group.response;

import it.chalmers.gamma.response.CustomResponseStatusException;
import org.springframework.http.HttpStatus;

public class GroupDoesNotExistResponse extends CustomResponseStatusException {
    public GroupDoesNotExistResponse() {
        super(HttpStatus.NOT_FOUND, "NO_SUCH_GROUP_EXISTS");
    }
}
