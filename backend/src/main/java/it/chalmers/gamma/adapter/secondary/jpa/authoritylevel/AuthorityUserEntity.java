package it.chalmers.gamma.adapter.secondary.jpa.authoritylevel;

import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import it.chalmers.gamma.domain.user.User;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "authority_user")
public class AuthorityUserEntity extends ImmutableEntity<AuthorityUserPK> {

    @EmbeddedId
    private AuthorityUserPK id;

    protected AuthorityUserEntity() {

    }

    protected AuthorityUserEntity(User user) {
    }

    @Override
    protected AuthorityUserPK id() {
        return this.id;
    }

    public User getIdentifier() {
        return null;
    }

}
