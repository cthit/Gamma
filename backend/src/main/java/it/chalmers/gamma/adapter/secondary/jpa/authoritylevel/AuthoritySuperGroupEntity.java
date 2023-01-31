package it.chalmers.gamma.adapter.secondary.jpa.authoritylevel;

import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "authority_super_group")
public class AuthoritySuperGroupEntity extends ImmutableEntity<AuthoritySuperGroupPK> {

    @EmbeddedId
    protected AuthoritySuperGroupPK id;

    protected AuthoritySuperGroupEntity() {
    }

    public AuthoritySuperGroupEntity(SuperGroupEntity superGroup, AuthorityLevelEntity authorityLevel) {
        this.id = new AuthoritySuperGroupPK(superGroup, authorityLevel);
    }

    @Override
    public AuthoritySuperGroupPK getId() {
        return this.id;
    }

    protected SuperGroupEntity getSuperGroup() {
        return this.id.superGroupEntity;
    }

}
