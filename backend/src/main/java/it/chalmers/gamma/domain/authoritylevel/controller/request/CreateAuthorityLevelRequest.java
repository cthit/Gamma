package it.chalmers.gamma.domain.authoritylevel.controller.request;

import it.chalmers.gamma.domain.authoritylevel.domain.AuthorityLevelName;

import java.util.Objects;
import javax.validation.constraints.NotEmpty;

public class CreateAuthorityLevelRequest {

    public AuthorityLevelName authorityLevel;

}
