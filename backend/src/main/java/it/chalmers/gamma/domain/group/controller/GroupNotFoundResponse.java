package it.chalmers.gamma.domain.group.controller;

import it.chalmers.gamma.util.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class GroupNotFoundResponse extends ErrorResponse {
    protected GroupNotFoundResponse() {
        super(HttpStatus.NOT_FOUND);
    }
}
