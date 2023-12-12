package it.chalmers.gamma.response.whitelist;

import it.chalmers.gamma.response.CustomResponseStatusException;
import org.springframework.http.HttpStatus;

public class WhitelistAlreadyAddedException extends CustomResponseStatusException {

    public WhitelistAlreadyAddedException() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "CID_ALREADY_ADDED_TO_WHITELIST");
    }
}
