package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NoDataSentResponse extends CustomResponseStatusException {
    public NoDataSentResponse() {
        super(HttpStatus.NOT_ACCEPTABLE, "NO_DATA_SENT");
    }
}
