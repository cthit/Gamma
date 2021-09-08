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
public class AuthoritySuperGroupPK extends Id<AuthoritySuperGroupPK.AuthoritySuperGroupPKDTO> {

    protected record AuthoritySuperGroupPKDTO(SuperGroup superGroup, AuthorityLevelName authorityLevelName) { }

    @JoinColumn(name = "super_group_id")
    @ManyToOne
    private SuperGroupEntity superGroupEntity;

    @Column(name = "authority_level")
    private String authorityLevel;

    protected AuthoritySuperGroupPK() {

    }

    public AuthoritySuperGroupPK(SuperGroupEntity superGroupEntity, String authorityLevel) {
        this.superGroupEntity = superGroupEntity;
        this.authorityLevel = authorityLevel;
    }

    @Override
    public AuthoritySuperGroupPKDTO value() {
        return new AuthoritySuperGroupPKDTO(
                this.superGroupEntity.toDomain(),
                AuthorityLevelName.valueOf(this.authorityLevel)
        );
    }
}
