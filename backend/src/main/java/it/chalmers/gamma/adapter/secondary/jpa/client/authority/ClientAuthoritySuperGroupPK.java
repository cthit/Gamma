package it.chalmers.gamma.adapter.secondary.jpa.client.authority;

import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.PKId;
import it.chalmers.gamma.app.client.domain.authority.AuthorityName;
import it.chalmers.gamma.app.client.domain.ClientUid;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupId;
import jakarta.persistence.*;

@Embeddable
public class ClientAuthoritySuperGroupPK extends PKId<ClientAuthoritySuperGroupPK.AuthoritySuperGroupPKDTO> {

    @JoinColumn(name = "super_group_id")
    @ManyToOne(fetch = FetchType.EAGER)
    protected SuperGroupEntity superGroupEntity;

    @Embedded
    protected ClientAuthorityEntityPK clientAuthority;

    protected ClientAuthoritySuperGroupPK() {

    }

    protected ClientAuthoritySuperGroupPK(SuperGroupEntity superGroupEntity,
                                          ClientAuthorityEntity clientAuthority) {
        this.superGroupEntity = superGroupEntity;
        this.clientAuthority = clientAuthority.getId();
    }

    @Override
    public AuthoritySuperGroupPKDTO getValue() {
        return new AuthoritySuperGroupPKDTO(
                new SuperGroupId(this.superGroupEntity.getId()),
                new AuthorityName(this.clientAuthority.name),
                new ClientUid(this.clientAuthority.client.getId())
        );
    }

    protected record AuthoritySuperGroupPKDTO(SuperGroupId superGroupId,
                                              AuthorityName authorityName,
                                              ClientUid clientUid) {
    }
}
