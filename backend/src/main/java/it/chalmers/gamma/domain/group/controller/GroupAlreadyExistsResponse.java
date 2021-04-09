package it.chalmers.gamma.domain.group.controller;

import it.chalmers.gamma.util.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class GroupAlreadyExistsResponse extends ErrorResponse {
    protected GroupAlreadyExistsResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
