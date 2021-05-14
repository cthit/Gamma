package it.chalmers.gamma.internal.authority.service.user;

import it.chalmers.gamma.internal.authoritylevel.service.AuthorityLevelName;
import it.chalmers.gamma.internal.user.service.UserId;
import it.chalmers.gamma.util.domain.abstraction.Id;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AuthorityUserPK extends Id<AuthorityUserPK.AuthorityUserPKDTO> {

    @Override
    protected AuthorityUserPKDTO get() {
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

    protected AuthorityUserPK(UserId userId, AuthorityLevelName authorityLevelName) {
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
