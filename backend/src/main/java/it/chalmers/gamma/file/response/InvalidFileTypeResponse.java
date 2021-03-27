package it.chalmers.gamma.file.response;

import it.chalmers.gamma.util.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class InvalidFileTypeResponse extends ErrorResponse {
    public InvalidFileTypeResponse() {
        super(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }
}
