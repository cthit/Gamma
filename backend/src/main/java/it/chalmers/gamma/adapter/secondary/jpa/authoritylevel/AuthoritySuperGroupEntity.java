package it.chalmers.gamma.adapter.secondary.jpa.authoritylevel;

import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import it.chalmers.gamma.app.domain.SuperGroup;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "authority_super_group")
public class AuthoritySuperGroupEntity extends ImmutableEntity<AuthoritySuperGroupPK> {

    @EmbeddedId
    private AuthoritySuperGroupPK id;

    protected AuthoritySuperGroupEntity() { }

    protected AuthoritySuperGroupEntity(SuperGroup superGroup) {
    }

    @Override
    protected AuthoritySuperGroupPK id() {
        return this.id;
    }
}
