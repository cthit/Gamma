package it.chalmers.gamma.response.authority;

import it.chalmers.gamma.domain.dto.authority.AuthorityDTO;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetAllAuthoritiesForLevelResponse {

    private final List<AuthorityDTO> authorities;
    private final String authorityLevel;

    public GetAllAuthoritiesForLevelResponse(List<AuthorityDTO> authorities, String authorityLevel) {
        this.authorities = authorities;
        this.authorityLevel = authorityLevel;
    }

    public List<AuthorityDTO> getAuthorities() {
        return this.authorities;
    }

    public String getAuthorityLevel() {
        return authorityLevel;
    }

    public GetAllAuthoritiesForLevelResponseObject toResponseObject() {
        return new GetAllAuthoritiesForLevelResponseObject(this);
    }

    public static class GetAllAuthoritiesForLevelResponseObject extends ResponseEntity<GetAllAuthoritiesForLevelResponse> {
        GetAllAuthoritiesForLevelResponseObject(GetAllAuthoritiesForLevelResponse body) {
            super(body, HttpStatus.ACCEPTED);
        }
    }
}
