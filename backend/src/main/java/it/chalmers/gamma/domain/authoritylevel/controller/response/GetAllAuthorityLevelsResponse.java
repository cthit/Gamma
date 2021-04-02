package it.chalmers.gamma.domain.authoritylevel.controller.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.domain.authoritylevel.domain.AuthorityLevelName;

public class GetAllAuthorityLevelsResponse {

    @JsonValue
    public final List<AuthorityLevelName> authorityLevels;

    public GetAllAuthorityLevelsResponse(List<AuthorityLevelName> authorityLevels) {
        this.authorityLevels = authorityLevels;
    }
}
