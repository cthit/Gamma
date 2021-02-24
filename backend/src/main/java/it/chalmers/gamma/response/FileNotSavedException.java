package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;

public class FileNotSavedException extends ErrorResponse {
    public FileNotSavedException() {
        super(HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
