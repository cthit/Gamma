package it.chalmers.gamma.internal.authorityuser.service;

import it.chalmers.gamma.util.domain.abstraction.ImmutableEntity;

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

    protected AuthorityUserEntity(AuthorityUserShallowDTO authorityUser) {
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
