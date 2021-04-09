package it.chalmers.gamma.domain.authority.controller;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.domain.authority.service.AuthorityDTO;
import java.util.List;

public class GetAllAuthoritiesResponse {

    @JsonValue
    private final List<AuthorityDTO> authorities;

    protected GetAllAuthoritiesResponse(List<AuthorityDTO> authorities) {
        this.authorities = authorities;
    }

}
