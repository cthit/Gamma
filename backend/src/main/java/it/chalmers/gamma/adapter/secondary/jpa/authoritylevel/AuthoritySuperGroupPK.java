package it.chalmers.gamma.adapter.secondary.jpa.authoritylevel;

import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.PKId;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupId;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class AuthoritySuperGroupPK extends PKId<AuthoritySuperGroupPK.AuthoritySuperGroupPKDTO> {

    @JoinColumn(name = "super_group_id")
    @ManyToOne(fetch = FetchType.LAZY)
    protected SuperGroupEntity superGroupEntity;

    @JoinColumn(name = "authority_level")
    @ManyToOne
    protected AuthorityLevelEntity authorityLevel;

    protected AuthoritySuperGroupPK() {

    }

    protected AuthoritySuperGroupPK(SuperGroupEntity superGroupEntity,
                                 AuthorityLevelEntity authorityLevelEntity) {
        this.superGroupEntity = superGroupEntity;
        this.authorityLevel = authorityLevelEntity;
    }

    protected record AuthoritySuperGroupPKDTO(SuperGroupId superGroupId,
                                              AuthorityLevelName authorityLevelName) { }

    @Override
    public AuthoritySuperGroupPKDTO getValue() {
        return new AuthoritySuperGroupPKDTO(
                new SuperGroupId(this.superGroupEntity.getId()),
                new AuthorityLevelName(this.authorityLevel.getId())
        );
    }
}
