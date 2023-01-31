package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.adapter.secondary.jpa.client.ClientEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.PKId;
import it.chalmers.gamma.app.client.domain.ClientUid;
import it.chalmers.gamma.app.user.domain.UserId;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Embeddable
public class UserApprovalEntityPK extends PKId<UserApprovalEntityPK.UserApprovalPKDTO> {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
    @ManyToOne
    @JoinColumn(name = "client_uid")
    private ClientEntity client;

    protected UserApprovalEntityPK() {
    }

    public UserApprovalEntityPK(UserEntity user, ClientEntity client) {
        this.user = user;
        this.client = client;
    }

    public UserEntity getUserEntity() {
        return this.user;
    }

    public ClientEntity getClientEntity() {
        return this.client;
    }

    @Override
    public UserApprovalPKDTO getValue() {
        return new UserApprovalPKDTO(
                new UserId(this.user.getId()),
                new ClientUid(this.client.getId())
        );
    }

    protected record UserApprovalPKDTO(UserId userId, ClientUid clientUid) {


    }
}
