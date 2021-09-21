package it.chalmers.gamma.adapter.secondary.jpa.authoritylevel;

import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntity;
import it.chalmers.gamma.domain.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.domain.supergroup.SuperGroup;
import it.chalmers.gamma.domain.supergroup.SuperGroupId;
import it.chalmers.gamma.domain.Id;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class AuthoritySuperGroupPK implements Id<AuthoritySuperGroupPK.AuthoritySuperGroupPKDTO> {

    protected record AuthoritySuperGroupPKDTO(SuperGroup superGroup, AuthorityLevelName authorityLevelName) { }

    @JoinColumn(name = "super_group_id")
    @ManyToOne
    private SuperGroupEntity superGroupEntity;

    @JoinColumn(name = "authority_level")
    @ManyToOne
    private AuthorityLevelEntity authorityLevel;

    protected AuthoritySuperGroupPK() {

    }

    public AuthoritySuperGroupPK(SuperGroupEntity superGroupEntity, AuthorityLevelEntity authorityLevelEntity) {
        this.superGroupEntity = superGroupEntity;
        this.authorityLevel = authorityLevelEntity;
    }

    @Override
    public AuthoritySuperGroupPKDTO getValue() {
        return new AuthoritySuperGroupPKDTO(
                this.superGroupEntity.toDomain(),
                this.authorityLevel.id()
        );
    }
}
