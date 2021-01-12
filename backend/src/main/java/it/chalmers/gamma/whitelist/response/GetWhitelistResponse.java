package it.chalmers.gamma.whitelist.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.whitelist.WhitelistDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetWhitelistResponse {
    @JsonUnwrapped
    private final WhitelistDTO whitelist;

    public GetWhitelistResponse(WhitelistDTO whitelist) {
        this.whitelist = whitelist;
    }

    public WhitelistDTO getWhitelist() {
        return this.whitelist;
    }

    public GetWhitelistResponseObject toResponseObject() {
        return new GetWhitelistResponseObject(this);
    }

    public static class GetWhitelistResponseObject extends ResponseEntity<GetWhitelistResponse> {
        GetWhitelistResponseObject(GetWhitelistResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
