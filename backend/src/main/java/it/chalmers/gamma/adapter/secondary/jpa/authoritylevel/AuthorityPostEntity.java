package it.chalmers.gamma.adapter.secondary.jpa.authoritylevel;

import it.chalmers.gamma.adapter.secondary.jpa.group.PostEntity;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import it.chalmers.gamma.app.domain.authoritylevel.AuthorityLevel;

import javax.persistence.*;

@Entity
@Table(name = "authority_post")
public class AuthorityPostEntity extends ImmutableEntity<AuthorityPostPK> {

    @EmbeddedId
    protected AuthorityPostPK id;

    protected AuthorityPostEntity() {}

    public AuthorityPostEntity(SuperGroupEntity superGroup, PostEntity postEntity, AuthorityLevelEntity authorityLevel) {
        this.id = new AuthorityPostPK(superGroup, postEntity, authorityLevel);
    }

    @Override
    protected AuthorityPostPK id() {
        return this.id;
    }

}

