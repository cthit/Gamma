package it.chalmers.gamma.file.response;

import it.chalmers.gamma.util.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class FileNotSavedResponse extends ErrorResponse {
    public FileNotSavedResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
