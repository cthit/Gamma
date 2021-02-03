package it.chalmers.gamma.authoritylevel.controller.response;

import it.chalmers.gamma.authoritylevel.dto.AuthorityLevelDTO;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetAllAuthorityLevelsResponse {
    private final List<AuthorityLevelDTO> authorityLevels;

    public GetAllAuthorityLevelsResponse(List<AuthorityLevelDTO> authorityLevels) {
        this.authorityLevels = authorityLevels;
    }

    public List<AuthorityLevelDTO> getAuthorityLevels() {
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
