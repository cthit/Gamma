package it.chalmers.delta.response;

import org.springframework.http.HttpStatus;

public class NoDataSentResponse extends CustomResponseStatusException {
    public NoDataSentResponse() {
        super(HttpStatus.NOT_ACCEPTABLE, "NO_DATA_SENT");
    }
}
