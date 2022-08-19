package it.chalmers.gamma.util.controller;

import it.chalmers.gamma.app.authentication.AccessGuard;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ErrorHandlingControllerAdvice {

    @ExceptionHandler({
            AccessGuard.AccessDeniedException.class,
            AccessDeniedException.class
    })
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public Error no() {
        return new Error(
                "ACCESS_DENIED",
                "ACCESS_DENIED"
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Error onIllegalArgumentException(IllegalArgumentException e) {
        return new Error(e.getStackTrace()[0].getClassName(), e.getMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Error asdf() {
        return new Error("asdf", "fdsa");
    }

    public record Error(String origin, String message) { }


}
