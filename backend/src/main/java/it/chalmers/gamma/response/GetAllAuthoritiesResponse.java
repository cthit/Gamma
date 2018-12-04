package it.chalmers.gamma.response;

import it.chalmers.gamma.db.entity.Authority;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetAllAuthoritiesResponse extends ResponseEntity<List<Authority>> {
    public GetAllAuthoritiesResponse(List<Authority> authorities) {
        super(authorities, HttpStatus.ACCEPTED);
    }
}
