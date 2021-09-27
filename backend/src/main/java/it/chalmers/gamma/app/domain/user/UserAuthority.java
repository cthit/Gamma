package it.chalmers.gamma.app.domain.user;

import it.chalmers.gamma.app.domain.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.app.domain.authoritylevel.AuthorityType;

public record UserAuthority(AuthorityLevelName authorityLevelName, AuthorityType authorityType) {
}
