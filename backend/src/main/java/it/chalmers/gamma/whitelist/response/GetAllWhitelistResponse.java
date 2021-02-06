package it.chalmers.gamma.whitelist.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;

import it.chalmers.gamma.domain.Cid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetAllWhitelistResponse {

    @JsonUnwrapped
    private final List<Cid> whitelist;

    public GetAllWhitelistResponse(List<Cid> whitelist) {
        this.whitelist = whitelist;
    }

    public List<Cid> getWhitelist() {
        return whitelist;
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
