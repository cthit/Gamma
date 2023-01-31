package it.chalmers.gamma.adapter.secondary.jpa.authoritylevel;

import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "authority_user")
public class AuthorityUserEntity extends ImmutableEntity<AuthorityUserPK> {

    @EmbeddedId
    private AuthorityUserPK id;

    protected AuthorityUserEntity() {

    }

    public AuthorityUserEntity(UserEntity user, AuthorityLevelEntity authorityLevel) {
        this.id = new AuthorityUserPK(user, authorityLevel);
    }

    @Override
    public AuthorityUserPK getId() {
        return this.id;
    }

    public UserEntity getUserEntity() {
        return this.id.getUserEntity();
    }

}
