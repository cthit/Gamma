package it.chalmers.gamma.adapter.secondary.jpa.authoritylevel;

import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import it.chalmers.gamma.app.domain.supergroup.SuperGroup;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "authority_super_group")
public class AuthoritySuperGroupEntity extends ImmutableEntity<AuthoritySuperGroupPK> {

    @EmbeddedId
    protected AuthoritySuperGroupPK id;

    protected AuthoritySuperGroupEntity() { }

    public AuthoritySuperGroupEntity(SuperGroupEntity superGroup, AuthorityLevelEntity authorityLevel) {
        this.id = new AuthoritySuperGroupPK(superGroup, authorityLevel);
    }

    @Override
    protected AuthoritySuperGroupPK id() {
        return this.id;
    }

}
