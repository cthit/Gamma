package it.chalmers.gamma.response;

import it.chalmers.gamma.db.entity.Authority;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetAuthorityResponse extends ResponseEntity<Authority> {
    public GetAuthorityResponse(Authority authority) {
        super(authority, HttpStatus.OK);
    }
}
