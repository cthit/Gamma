package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;

public class InputValidationFailedResponse extends CustomResponseStatusException{

    public InputValidationFailedResponse(String errors){
        super(HttpStatus.UNPROCESSABLE_ENTITY, errors);
    }
}
