package it.chalmers.gamma.adapter.secondary.jpa.authorityuser;

import it.chalmers.gamma.app.domain.AuthorityLevelName;
import it.chalmers.gamma.app.domain.UserId;
import it.chalmers.gamma.app.domain.Id;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
public class AuthorityUserPK extends Id<AuthorityUserPK.AuthorityUserPKDTO> {

    @Override
    protected AuthorityUserPKDTO value() {
        return new AuthorityUserPKDTO(
                this.userId,
                this.authorityLevelName
        );
    }

    protected record AuthorityUserPKDTO(UserId userId, AuthorityLevelName authorityLevelName) { }

    @Embedded
    private UserId userId;

    @Embedded
    private AuthorityLevelName authorityLevelName;

    protected AuthorityUserPK() {

    }

    public AuthorityUserPK(UserId userId, AuthorityLevelName authorityLevelName) {
        this.userId = userId;
        this.authorityLevelName = authorityLevelName;
    }

    protected UserId getUserId() {
        return userId;
    }

    protected AuthorityLevelName getAuthorityLevelName() {
        return authorityLevelName;
    }

}
