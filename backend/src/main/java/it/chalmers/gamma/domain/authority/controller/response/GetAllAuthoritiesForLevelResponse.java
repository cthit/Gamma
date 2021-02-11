package it.chalmers.gamma.domain.authority.controller.response;

import it.chalmers.gamma.domain.authority.data.AuthorityDTO;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetAllAuthoritiesForLevelResponse {

    private final List<AuthorityDTO> authorities;

    public GetAllAuthoritiesForLevelResponse(List<AuthorityDTO> authorities) {
        this.authorities = authorities;
    }

    public List<AuthorityDTO> getAuthorities() {
        return this.authorities;
    }

    public GetAllAuthoritiesForLevelResponseObject toResponseObject() {
        return new GetAllAuthoritiesForLevelResponseObject(this);
    }

    public static class GetAllAuthoritiesForLevelResponseObject
            extends ResponseEntity<GetAllAuthoritiesForLevelResponse> {
        GetAllAuthoritiesForLevelResponseObject(GetAllAuthoritiesForLevelResponse body) {
            super(body, HttpStatus.ACCEPTED);
        }
    }
}
