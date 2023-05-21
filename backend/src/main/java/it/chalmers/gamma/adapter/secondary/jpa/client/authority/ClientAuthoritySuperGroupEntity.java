package it.chalmers.gamma.adapter.secondary.jpa.client.authority;

import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "client_authority_super_group")
public class ClientAuthoritySuperGroupEntity extends ImmutableEntity<ClientAuthoritySuperGroupPK> {

    @EmbeddedId
    protected ClientAuthoritySuperGroupPK id;

    protected ClientAuthoritySuperGroupEntity() {
    }

    public ClientAuthoritySuperGroupEntity(SuperGroupEntity superGroup, ClientAuthorityEntity clientAuthorityEntity) {
        this.id = new ClientAuthoritySuperGroupPK(superGroup, clientAuthorityEntity);
    }

    @Override
    public ClientAuthoritySuperGroupPK getId() {
        return this.id;
    }

    protected SuperGroupEntity getSuperGroup() {
        return this.id.superGroupEntity;
    }

}
