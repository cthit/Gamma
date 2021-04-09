package it.chalmers.gamma.domain.authoritylevel.service;

import it.chalmers.gamma.util.domain.abstraction.BaseEntity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "authority_level")
public class AuthorityLevel extends BaseEntity<AuthorityLevelName> {

    @EmbeddedId
    private AuthorityLevelName authorityLevel;

    protected AuthorityLevel() {}

    protected AuthorityLevel(AuthorityLevelName authorityLevel) {
        this.authorityLevel = authorityLevel;
    }

    @Override
    protected AuthorityLevelName toDTO() {
        return authorityLevel;
    }
}
