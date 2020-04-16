package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;

public class FileNotFoundResponse extends CustomResponseStatusException{
    public FileNotFoundResponse() {
        super(HttpStatus.NOT_FOUND, "FILE_NOT_FOUND");
    }
}
