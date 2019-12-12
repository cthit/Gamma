package it.chalmers.gamma.response.whitelist;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetAllWhitelistResponse {
    @JsonValue
    private final List<GetWhitelistResponse> whitelistResponses;

    public GetAllWhitelistResponse(List<GetWhitelistResponse> whitelistResponses) {
        this.whitelistResponses = whitelistResponses;
    }

    public List<GetWhitelistResponse> getWhitelistResponses() {
        return this.whitelistResponses;
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
