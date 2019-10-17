package it.chalmers.delta.response;

import it.chalmers.delta.db.entity.Authority;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetAuthorityResponse extends ResponseEntity<Authority> {
    public GetAuthorityResponse(Authority authority) {
        super(authority, HttpStatus.OK);
    }
}
