package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;

public class InvalidFileTypeResponse extends ErrorResponse {
    public InvalidFileTypeResponse() {
        super(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }
}
