package it.chalmers.gamma.domain.whitelist.controller;

import it.chalmers.gamma.util.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class CidNotWhitelistedResponse extends ErrorResponse {
    public CidNotWhitelistedResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
