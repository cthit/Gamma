package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class GroupAlreadyExistsResponse extends CustomResponseStatusException
{
    public GroupAlreadyExistsResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "GROUP_ALREADY_EXISTS");
    }

}
