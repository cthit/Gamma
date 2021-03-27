package it.chalmers.gamma.file.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class FileTooLargeResponse extends ResponseEntity<String> {
    public FileTooLargeResponse() {
        super("FILE_UPLOAD_TOO_LARGE", HttpStatus.PAYLOAD_TOO_LARGE);
    }
}
