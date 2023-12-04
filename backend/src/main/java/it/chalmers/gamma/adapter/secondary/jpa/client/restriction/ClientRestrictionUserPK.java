package it.chalmers.gamma.adapter.secondary.jpa.client.restriction;

import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.PKId;
import it.chalmers.gamma.app.client.domain.ClientUid;
import it.chalmers.gamma.app.client.domain.restriction.ClientRestrictionId;
import it.chalmers.gamma.app.user.domain.UserId;
import jakarta.persistence.*;

@Embeddable
public class ClientRestrictionUserPK extends PKId<ClientRestrictionUserPK.ClientRestrictionPKDTO> {

    @ManyToOne
    @JoinColumn(name = "restriction_id")
    private ClientRestrictionEntity clientRestriction;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private UserEntity userEntity;

    protected ClientRestrictionUserPK() {
    }

    protected ClientRestrictionUserPK(ClientRestrictionEntity clientRestrictionEntity, UserEntity userEntity) {
        this.clientRestriction = clientRestrictionEntity;
        this.userEntity = userEntity;
    }

    @Override
    public ClientRestrictionPKDTO getValue() {
        return new ClientRestrictionPKDTO(
                new ClientRestrictionId(this.clientRestriction.restrictionId),
                new UserId(this.userEntity.getId())
        );
    }

    protected record ClientRestrictionPKDTO(ClientRestrictionId clientRestrictionId,
                                            UserId userId) {
    }

}
