package it.chalmers.gamma.response.whitelist;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class WhitelistIsValidResponse {
    private final Boolean valid;

    public WhitelistIsValidResponse(Boolean valid) {
        this.valid = valid;
    }

    public Boolean getValid() {
        return valid;
    }

    public WhitelistIsValidResponseObject getResponseObject() {
        return new WhitelistIsValidResponseObject(this);
    }

    public static class WhitelistIsValidResponseObject extends ResponseEntity<WhitelistIsValidResponse> {
        WhitelistIsValidResponseObject(WhitelistIsValidResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
