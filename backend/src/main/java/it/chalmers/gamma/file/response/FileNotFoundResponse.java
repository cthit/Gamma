package it.chalmers.gamma.file.response;

import it.chalmers.gamma.util.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class FileNotFoundResponse extends ErrorResponse {
    public FileNotFoundResponse() {
        super(HttpStatus.NOT_FOUND);
    }
}
