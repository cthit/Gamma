package it.chalmers.gamma.domain.authority.controller.response;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.domain.authority.data.dto.AuthorityDTO;
import java.util.List;

public class GetAllAuthoritiesResponse {

    @JsonValue
    public final List<AuthorityDTO> authorities;

    public GetAllAuthoritiesResponse(List<AuthorityDTO> authorities) {
        this.authorities = authorities;
    }

}
