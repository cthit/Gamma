package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;

public class NoDataSentResponse extends CustomResponseStatusException {
    public NoDataSentResponse() {
        super(HttpStatus.NOT_ACCEPTABLE, "NO_DATA_SENT");
    }
}
