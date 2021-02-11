package it.chalmers.gamma.domain.authoritylevel.controller.response;

import java.util.List;

import it.chalmers.gamma.domain.authoritylevel.AuthorityLevelName;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetAllAuthorityLevelsResponse {
    private final List<AuthorityLevelName> authorityLevels;

    public GetAllAuthorityLevelsResponse(List<AuthorityLevelName> authorityLevels) {
        this.authorityLevels = authorityLevels;
    }

    public List<AuthorityLevelName> getAuthorityLevels() {
        return this.authorityLevels;
    }

    public GetAllAuthorityLevelsResponseObject toResponseObject() {
        return new GetAllAuthorityLevelsResponseObject(this);
    }

    public static class GetAllAuthorityLevelsResponseObject extends ResponseEntity<GetAllAuthorityLevelsResponse> {
        GetAllAuthorityLevelsResponseObject(GetAllAuthorityLevelsResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
