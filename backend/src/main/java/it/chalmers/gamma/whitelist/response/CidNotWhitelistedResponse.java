package it.chalmers.gamma.whitelist.response;

import it.chalmers.gamma.response.CustomResponseStatusException;
import org.springframework.http.HttpStatus;

public class CidNotWhitelistedResponse extends CustomResponseStatusException {
    public CidNotWhitelistedResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "CID_NOT_WHITELISTED");
    }
}
