package it.chalmers.gamma.file.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetFileResponse extends ResponseEntity<byte[]> {
    public GetFileResponse(byte[] file) {
        super(file, HttpStatus.OK);
    }
}
