package it.chalmers.gamma.adapter.secondary.jpa.authoritylevel;

import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import it.chalmers.gamma.app.domain.AuthorityLevel;

import javax.persistence.*;

@Entity
@Table(name = "authority_post")
public class AuthorityPostEntity extends ImmutableEntity<AuthorityPostPK> {

    @EmbeddedId
    private AuthorityPostPK id;

    protected AuthorityPostEntity() {}

    protected AuthorityPostEntity(AuthorityLevel.SuperGroupPost superGroupPost) {

    }

    @Override
    protected AuthorityPostPK id() {
        return this.id;
    }

}

