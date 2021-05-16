package it.chalmers.gamma.internal.authority.supergroup.service;

import it.chalmers.gamma.internal.authority.level.service.AuthorityLevelName;
import it.chalmers.gamma.internal.supergroup.service.SuperGroupId;
import it.chalmers.gamma.util.domain.abstraction.Id;

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
    protected AuthoritySuperGroupPKDTO get() {
        return new AuthoritySuperGroupPKDTO(
                this.superGroupId,
                this.authorityLevelName
        );
    }
}
