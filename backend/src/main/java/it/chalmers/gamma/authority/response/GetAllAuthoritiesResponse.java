package it.chalmers.gamma.authority.response;

import it.chalmers.gamma.authority.AuthorityDTO;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetAllAuthoritiesResponse {

    private final List<AuthorityDTO> authorities;

    public GetAllAuthoritiesResponse(List<AuthorityDTO> authorities) {
        this.authorities = authorities;
    }

    public List<AuthorityDTO> getAuthorities() {
        return this.authorities;
    }

    public GetAllAuthoritiesResponseObject toResponseObject() {
        return new GetAllAuthoritiesResponseObject(this);
    }

    public static class GetAllAuthoritiesResponseObject extends ResponseEntity<GetAllAuthoritiesResponse> {
        GetAllAuthoritiesResponseObject(GetAllAuthoritiesResponse body) {
            super(body, HttpStatus.ACCEPTED);
        }
    }
}
