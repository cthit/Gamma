package it.chalmers.gamma.util;

import it.chalmers.gamma.response.CustomResponseStatusException;
import org.springframework.http.HttpStatus;

public class InternalServerErrorResponse extends CustomResponseStatusException {
    public InternalServerErrorResponse() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "Somthing went wrong, please contact digIT@chalmers.it if the error persist");
    }
}
