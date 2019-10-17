package it.chalmers.delta.response;

import org.springframework.http.HttpStatus;

public class CIDAlreadyWhitelistedResponse extends CustomResponseStatusException {

    public CIDAlreadyWhitelistedResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "CID_ALREADY_ADDED_TO_WHITELIST");
    }
}
