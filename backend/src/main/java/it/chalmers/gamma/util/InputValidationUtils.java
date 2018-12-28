package it.chalmers.gamma.util;

import org.springframework.validation.ObjectError;

import java.util.List;

public final class InputValidationUtils {
    private InputValidationUtils(){

    }

    public static String getErrorMessages(List<ObjectError> errors){
        StringBuilder errorMessages = new StringBuilder();
        for(ObjectError error : errors){
            errorMessages.append(error.getObjectName());
        }
        return errorMessages.toString();
    }
}
