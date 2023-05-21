package it.chalmers.gamma.adapter.secondary.jpa.client.restriction;

import it.chalmers.gamma.adapter.secondary.jpa.client.ClientEntity;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.PKId;
import it.chalmers.gamma.app.authority.domain.AuthorityName;
import it.chalmers.gamma.app.client.domain.ClientUid;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupId;
import jakarta.persistence.*;

@Embeddable
public class ClientRestrictionSuperGroupPK extends PKId<ClientRestrictionSuperGroupPK.ClientRestrictionPKDTO> {

    @ManyToOne
    @JoinColumn(name = "client_uid")
    private ClientEntity client;

    @JoinColumn(name = "super_group_id")
    @ManyToOne(fetch = FetchType.EAGER)
    protected SuperGroupEntity superGroupEntity;

    protected ClientRestrictionSuperGroupPK() {
    }

    protected ClientRestrictionSuperGroupPK(ClientEntity clientEntity, SuperGroupEntity superGroupEntity) {
        this.client = clientEntity;
        this.superGroupEntity = superGroupEntity;
    }

    @Override
    public ClientRestrictionPKDTO getValue() {
        return new ClientRestrictionPKDTO(
                new ClientUid(this.client.getId()),
                new SuperGroupId(this.superGroupEntity.getId())
        );
    }

    protected record ClientRestrictionPKDTO(ClientUid clientUid,
                                            SuperGroupId superGroupId) {
    }

}
