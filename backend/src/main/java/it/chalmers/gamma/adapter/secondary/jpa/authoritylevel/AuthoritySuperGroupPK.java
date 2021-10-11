package it.chalmers.gamma.adapter.secondary.jpa.authoritylevel;

import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntity;
import it.chalmers.gamma.app.domain.PKId;
import it.chalmers.gamma.app.domain.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.app.domain.supergroup.SuperGroupId;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class AuthoritySuperGroupPK extends PKId<AuthoritySuperGroupPK.AuthoritySuperGroupPKDTO> {

    @JoinColumn(name = "super_group_id")
    @ManyToOne
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
                this.superGroupEntity.domainId(),
                this.authorityLevel.domainId()
        );
    }
}
