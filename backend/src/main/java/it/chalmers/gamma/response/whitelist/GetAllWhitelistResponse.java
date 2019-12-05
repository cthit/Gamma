package it.chalmers.gamma.response.whitelist;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetAllWhitelistResponse {
    private final List<GetWhitelistResponse> whitelistResponses;

    public GetAllWhitelistResponse(List<GetWhitelistResponse> whitelistResponses) {
        this.whitelistResponses = whitelistResponses;
    }

    public List<GetWhitelistResponse> getWhitelistResponses() {
        return whitelistResponses;
    }

    public GetAllWhitelistResponseObject toResponseObject() {
        return new GetAllWhitelistResponseObject(this);
    }

    public static class GetAllWhitelistResponseObject extends ResponseEntity<GetAllWhitelistResponse> {
        GetAllWhitelistResponseObject(GetAllWhitelistResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
