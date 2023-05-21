package it.chalmers.gamma.adapter.secondary.jpa.client.restriction;

import it.chalmers.gamma.adapter.secondary.jpa.client.ClientEntity;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.PKId;
import it.chalmers.gamma.app.authority.domain.AuthorityName;
import it.chalmers.gamma.app.client.domain.ClientUid;
import it.chalmers.gamma.app.user.domain.UserId;
import jakarta.persistence.*;

@Embeddable
public class ClientRestrictionUserPK extends PKId<ClientRestrictionUserPK.ClientRestrictionPKDTO> {

    @ManyToOne
    @JoinColumn(name = "client_uid")
    private ClientEntity client;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private UserEntity userEntity;

    protected ClientRestrictionUserPK() {
    }

    protected ClientRestrictionUserPK(ClientEntity clientEntity, UserEntity userEntity) {
        this.client = clientEntity;
        this.userEntity = userEntity;
    }

    @Override
    public ClientRestrictionPKDTO getValue() {
        return new ClientRestrictionPKDTO(
                new ClientUid(this.client.getId()),
                new UserId(this.userEntity.getId())
        );
    }

    protected record ClientRestrictionPKDTO(ClientUid clientUid,
                                            UserId userId) {
    }

}
