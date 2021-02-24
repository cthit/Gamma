package it.chalmers.gamma.util;

import it.chalmers.gamma.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class InternalServerErrorResponse extends ErrorResponse {
    public InternalServerErrorResponse() {
        super(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
