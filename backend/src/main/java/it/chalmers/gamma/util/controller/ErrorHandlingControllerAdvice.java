package it.chalmers.gamma.util.controller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class ErrorHandlingControllerAdvice {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationErrorResponse onConstraintValidationException(
            ConstraintViolationException e) {
        List<Violation> errors = new ArrayList<>();
        for (ConstraintViolation violation : e.getConstraintViolations()) {
            errors.add(new Violation(violation.getPropertyPath().toString(), violation.getMessage()));
        }
        return new ValidationErrorResponse(errors);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Error onIllegalArgumentException(IllegalArgumentException e) {
        return new Error(e.getStackTrace()[0].getClassName(), e.getMessage());
    }

    public record Error(String origin, String message) { }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ValidationErrorResponse onMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        List<Violation> errors = new ArrayList<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errors.add(new Violation(fieldError.getField(), fieldError.getDefaultMessage()));
        }
        return new ValidationErrorResponse(errors);
    }

    public record ValidationErrorResponse(
            List<Violation> violations) { }

    public record Violation(String fieldName, String message) { }


}
