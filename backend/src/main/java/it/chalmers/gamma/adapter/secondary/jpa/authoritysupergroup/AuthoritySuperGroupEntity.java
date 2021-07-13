package it.chalmers.gamma.adapter.secondary.jpa.authoritysupergroup;

import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import it.chalmers.gamma.app.authoritysupergroup.service.AuthoritySuperGroupDTO;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "authority_super_group")
public class AuthoritySuperGroupEntity extends ImmutableEntity<AuthoritySuperGroupPK, AuthoritySuperGroupDTO> {

    @EmbeddedId
    private AuthoritySuperGroupPK id;

    protected AuthoritySuperGroupEntity() { }

    protected AuthoritySuperGroupEntity(AuthoritySuperGroupDTO authority) {
        this.id = new AuthoritySuperGroupPK(
                authority.superGroupId(),
                authority.authorityLevelName()
        );
    }

    @Override
    protected AuthoritySuperGroupDTO toDTO() {
        return new AuthoritySuperGroupDTO(this.id.value().superGroupId(), this.id.value().authorityLevelName());
    }

    @Override
    protected AuthoritySuperGroupPK id() {
        return this.id;
    }
}
