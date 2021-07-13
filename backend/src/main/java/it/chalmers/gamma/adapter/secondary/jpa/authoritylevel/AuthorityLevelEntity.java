package it.chalmers.gamma.adapter.secondary.jpa.authoritylevel;

import it.chalmers.gamma.app.domain.AuthorityLevelName;
import it.chalmers.gamma.adapter.secondary.jpa.util.SingleImmutableEntity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "authority_level")
public class AuthorityLevelEntity extends SingleImmutableEntity<AuthorityLevelName> {

    @Id
    @Column(name = "autority_level")
    private String authorityLevel;

    protected AuthorityLevelEntity() {}

    protected AuthorityLevelEntity(AuthorityLevelName authorityLevel) {
        this.authorityLevel = authorityLevel.value();
    }

    @Override
    protected AuthorityLevelName get() {
        return AuthorityLevelName.valueOf(this.authorityLevel);
    }
}
