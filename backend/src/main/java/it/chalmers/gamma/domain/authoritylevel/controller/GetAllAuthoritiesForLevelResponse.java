package it.chalmers.gamma.domain.authoritylevel.controller;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.domain.authority.service.AuthorityDTO;

import java.util.List;

public class GetAllAuthoritiesForLevelResponse {

    @JsonValue
    private final List<AuthorityDTO> authorities;

    protected GetAllAuthoritiesForLevelResponse(List<AuthorityDTO> authorities) {
        this.authorities = authorities;
    }

}
