package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;

public class FileNotFoundResponse extends ErrorResponse {
    public FileNotFoundResponse() {
        super(HttpStatus.NOT_FOUND);
    }
}
