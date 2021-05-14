package it.chalmers.gamma.internal.authority.service.user;

import it.chalmers.gamma.util.domain.abstraction.ImmutableEntity;
import it.chalmers.gamma.util.domain.abstraction.MutableEntity;
import it.chalmers.gamma.util.domain.abstraction.Id;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "authority_user")
public class AuthorityUser extends ImmutableEntity<AuthorityUserPK, AuthorityUserShallowDTO> {

    @EmbeddedId
    private AuthorityUserPK id;

    protected AuthorityUser() {

    }

    protected AuthorityUser(AuthorityUserShallowDTO authorityUser) {
        this.id = new AuthorityUserPK(authorityUser.userId(), authorityUser.authorityLevelName());
    }

    @Override
    protected AuthorityUserShallowDTO toDTO() {
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
