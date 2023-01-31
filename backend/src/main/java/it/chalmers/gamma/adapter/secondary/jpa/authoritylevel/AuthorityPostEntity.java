package it.chalmers.gamma.adapter.secondary.jpa.authoritylevel;

import it.chalmers.gamma.adapter.secondary.jpa.group.PostEntity;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "authority_post")
public class AuthorityPostEntity extends ImmutableEntity<AuthorityPostPK> {

    @EmbeddedId
    protected AuthorityPostPK id;

    protected AuthorityPostEntity() {
    }

    public AuthorityPostEntity(SuperGroupEntity superGroup, PostEntity postEntity, AuthorityLevelEntity authorityLevel) {
        this.id = new AuthorityPostPK(superGroup, postEntity, authorityLevel);
    }

    @Override
    public AuthorityPostPK getId() {
        return this.id;
    }

    public PostEntity getPost() {
        return this.id.postEntity;
    }

    public SuperGroupEntity getSuperGroupEntity() {
        return this.id.superGroupEntity;
    }

}

