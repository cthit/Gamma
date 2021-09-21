package it.chalmers.gamma.domain.user;

import it.chalmers.gamma.adapter.secondary.userdetails.GrantedAuthorityProxy;
import it.chalmers.gamma.domain.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.domain.authoritylevel.AuthorityType;

public record UserAuthority(AuthorityLevelName authorityLevelName, AuthorityType authorityType) {
}
