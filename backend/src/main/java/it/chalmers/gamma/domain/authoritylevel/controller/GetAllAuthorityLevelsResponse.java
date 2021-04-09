package it.chalmers.gamma.domain.authoritylevel.controller;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.domain.authoritylevel.service.AuthorityLevelName;

public class GetAllAuthorityLevelsResponse {

    @JsonValue
    private final List<AuthorityLevelName> authorityLevels;

    protected GetAllAuthorityLevelsResponse(List<AuthorityLevelName> authorityLevels) {
        this.authorityLevels = authorityLevels;
    }
}
