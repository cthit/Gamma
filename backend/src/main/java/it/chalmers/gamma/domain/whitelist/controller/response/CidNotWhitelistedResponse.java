package it.chalmers.gamma.domain.whitelist.controller.response;

import it.chalmers.gamma.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class CidNotWhitelistedResponse extends ErrorResponse {
    public CidNotWhitelistedResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
