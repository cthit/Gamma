package it.chalmers.delta.response;

import org.springframework.http.HttpStatus;

public class FileNotSavedException extends CustomResponseStatusException {
    public FileNotSavedException() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "FILE_COULD_NOT_BE_SAVED");
    }
}
