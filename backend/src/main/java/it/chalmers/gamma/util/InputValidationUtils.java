package it.chalmers.gamma.util;

import java.util.List;
import org.springframework.validation.ObjectError;

public final class InputValidationUtils {
    private InputValidationUtils() {

    }

    public static String getErrorMessages(List<ObjectError> errors) {
        StringBuilder errorMessages = new StringBuilder();
        for (ObjectError error : errors) {
            errorMessages.append(error.getDefaultMessage());
        }
        return errorMessages.toString();
    }
}
