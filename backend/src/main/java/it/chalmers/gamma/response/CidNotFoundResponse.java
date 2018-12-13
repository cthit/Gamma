package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CidNotFoundResponse extends CustomResponseStatusException {
    public CidNotFoundResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "NO_CID_FOUND");
    }
}
