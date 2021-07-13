package it.chalmers.gamma.util.controller;

import it.chalmers.gamma.app.AccessGuard;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ErrorHandlingControllerAdvice {

    @ExceptionHandler(AccessGuard.AccessDeniedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String no() {
        return "no";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Error onIllegalArgumentException(IllegalArgumentException e) {
        return new Error(e.getStackTrace()[0].getClassName(), e.getMessage());
    }

    public record Error(String origin, String message) { }


}
