package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;

public class InvalidFileTypeResponse extends CustomResponseStatusException{
    public InvalidFileTypeResponse() {
        super(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "INVALID_FILE_TYPE");
    }
}
