package it.chalmers.gamma.util;

import org.springframework.validation.ObjectError;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

public class InputValidationUtils {
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
