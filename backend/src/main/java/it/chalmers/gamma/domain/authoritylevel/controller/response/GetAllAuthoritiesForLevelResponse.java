package it.chalmers.gamma.domain.authoritylevel.controller.response;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.domain.authority.data.dto.AuthorityDTO;

import java.util.List;

public class GetAllAuthoritiesForLevelResponse {

    @JsonValue
    public final List<AuthorityDTO> authorities;

    public GetAllAuthoritiesForLevelResponse(List<AuthorityDTO> authorities) {
        this.authorities = authorities;
    }

}
