package it.chalmers.gamma.response;

import it.chalmers.gamma.db.entity.Whitelist;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetWhitelistedResponse extends ResponseEntity<List<Whitelist>> {
    public GetWhitelistedResponse(List<Whitelist> whitelist) {
        super(whitelist, HttpStatus.ACCEPTED);
    }
}
