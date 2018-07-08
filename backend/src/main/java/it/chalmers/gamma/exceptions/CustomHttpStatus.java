package it.chalmers.gamma.exceptions;

import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

public class CustomHttpStatus{
    private int code;
    private String status;
    private String message;
    private List<String> errors;

    public CustomHttpStatus(int code, String status, String message, List<String> errors){
        this.code = code;
        this.status = status;
        this.message = message;
        this.errors = new ArrayList<String>();
        this.errors.addAll(errors);
    }

    public CustomHttpStatus(int code, String status, String message, String error){
        this.code = code;
        this.status = status;
        this.message = message;
        this.errors = new ArrayList<String>();

        errors.add(error);
    }

    public int getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getErrors() {
        return errors;
    }

}
