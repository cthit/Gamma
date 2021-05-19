package it.chalmers.gamma.internal.authority.level.service;

import it.chalmers.gamma.util.domain.abstraction.SingleImmutableEntity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "authority_level")
public class AuthorityLevel extends SingleImmutableEntity<AuthorityLevelName> {

    @EmbeddedId
    private AuthorityLevelName authorityLevel;

    protected AuthorityLevel() {}

    protected AuthorityLevel(AuthorityLevelName authorityLevel) {
        this.authorityLevel = authorityLevel;
    }

    @Override
    protected AuthorityLevelName get() {
        return authorityLevel;
    }
}