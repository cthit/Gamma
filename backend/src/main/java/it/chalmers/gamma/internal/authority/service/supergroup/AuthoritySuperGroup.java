package it.chalmers.gamma.internal.authority.service.supergroup;

import it.chalmers.gamma.util.domain.abstraction.ImmutableEntity;
import it.chalmers.gamma.util.domain.abstraction.MutableEntity;
import it.chalmers.gamma.util.domain.abstraction.Id;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "authority_super_group")
public class AuthoritySuperGroup extends ImmutableEntity<AuthoritySuperGroupPK, AuthoritySuperGroupShallowDTO> {

    @EmbeddedId
    private AuthoritySuperGroupPK id;

    @Override
    protected AuthoritySuperGroupShallowDTO toDTO() {
        return new AuthoritySuperGroupShallowDTO(this.id.get().superGroupId(), this.id.get().authorityLevelName());
    }

    @Override
    protected AuthoritySuperGroupPK id() {
        return this.id;
    }
}
