package it.chalmers.gamma.app.authoritylevel.service;

import it.chalmers.gamma.app.domain.AuthorityLevelName;
import it.chalmers.gamma.util.entity.SingleImmutableEntity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "authority_level")
public class AuthorityLevelEntity extends SingleImmutableEntity<AuthorityLevelName> {

    @EmbeddedId
    private AuthorityLevelName authorityLevel;

    protected AuthorityLevelEntity() {}

    protected AuthorityLevelEntity(AuthorityLevelName authorityLevel) {
        this.authorityLevel = authorityLevel;
    }

    @Override
    protected AuthorityLevelName get() {
        return authorityLevel;
    }
}
