package it.chalmers.gamma.security.authentication.response;

import it.chalmers.gamma.util.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class InvalidJWTTokenResponse extends ErrorResponse {

    public InvalidJWTTokenResponse() {
        super(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
