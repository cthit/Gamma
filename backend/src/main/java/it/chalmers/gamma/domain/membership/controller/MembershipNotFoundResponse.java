package it.chalmers.gamma.domain.membership.controller;

import it.chalmers.gamma.util.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class MembershipNotFoundResponse extends ErrorResponse {

    protected MembershipNotFoundResponse() {
        super(HttpStatus.NOT_FOUND);
    }

}
