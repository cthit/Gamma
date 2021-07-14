package it.chalmers.gamma.adapter.secondary.jpa.authoritylevel;

import it.chalmers.gamma.app.domain.AuthorityLevelName;
import it.chalmers.gamma.app.domain.SuperGroupId;
import it.chalmers.gamma.app.domain.Id;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
public class AuthoritySuperGroupPK extends Id<AuthoritySuperGroupPK.AuthoritySuperGroupPKDTO> {

    protected record AuthoritySuperGroupPKDTO(SuperGroupId superGroupId, AuthorityLevelName authorityLevelName) { }

    @Embedded
    private SuperGroupId superGroupId;

    @Embedded
    private AuthorityLevelName authorityLevelName;

    protected AuthoritySuperGroupPK() {

    }

    public AuthoritySuperGroupPK(SuperGroupId superGroupId, AuthorityLevelName authorityLevelName) {
        this.superGroupId = superGroupId;
        this.authorityLevelName = authorityLevelName;
    }

    @Override
    protected AuthoritySuperGroupPKDTO value() {
        return new AuthoritySuperGroupPKDTO(
                this.superGroupId,
                this.authorityLevelName
        );
    }
}
