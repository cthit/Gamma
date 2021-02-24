package it.chalmers.gamma.domain.group.controller.response;

import it.chalmers.gamma.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class GroupNotFoundResponse extends ErrorResponse {
    public GroupNotFoundResponse() {
        super(HttpStatus.NOT_FOUND);
    }
}
