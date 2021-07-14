package it.chalmers.gamma.adapter.secondary.jpa.authoritylevel;

import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import it.chalmers.gamma.app.authorityuser.service.AuthorityUserShallowDTO;
import it.chalmers.gamma.app.domain.User;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "authority_user")
public class AuthorityUserEntity extends ImmutableEntity<AuthorityUserPK, AuthorityUserShallowDTO> {

    @EmbeddedId
    private AuthorityUserPK id;

    protected AuthorityUserEntity() {

    }

    protected AuthorityUserEntity(User user) {
    }

    @Override
    protected AuthorityUserShallowDTO toDomain() {
        return new AuthorityUserShallowDTO(
                this.id.getUserId(),
                this.id.getAuthorityLevelName()
        );
    }

    @Override
    protected AuthorityUserPK id() {
        return this.id;
    }
}
