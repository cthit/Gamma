package it.chalmers.gamma.domain.group.controller.response;

import it.chalmers.gamma.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class GroupAlreadyExistsResponse extends ErrorResponse {
    public GroupAlreadyExistsResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
