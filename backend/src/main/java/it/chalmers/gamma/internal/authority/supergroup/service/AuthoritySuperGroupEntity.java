package it.chalmers.gamma.internal.authority.supergroup.service;

import it.chalmers.gamma.util.domain.abstraction.ImmutableEntity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "authority_super_group")
public class AuthoritySuperGroupEntity extends ImmutableEntity<AuthoritySuperGroupPK, AuthoritySuperGroupShallowDTO> {

    @EmbeddedId
    private AuthoritySuperGroupPK id;

    protected AuthoritySuperGroupEntity() { }

    protected AuthoritySuperGroupEntity(AuthoritySuperGroupShallowDTO authority) {
        this.id = new AuthoritySuperGroupPK(
                authority.superGroupId(),
                authority.authorityLevelName()
        );
    }

    @Override
    protected AuthoritySuperGroupShallowDTO toDTO() {
        return new AuthoritySuperGroupShallowDTO(this.id.get().superGroupId(), this.id.get().authorityLevelName());
    }

    @Override
    protected AuthoritySuperGroupPK id() {
        return this.id;
    }
}
