package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;

import java.util.List;

public class InputValidationFailedResponse extends CustomResponseStatusException{

    public InputValidationFailedResponse(String errors){
        super(HttpStatus.UNPROCESSABLE_ENTITY, errors);
    }
}
