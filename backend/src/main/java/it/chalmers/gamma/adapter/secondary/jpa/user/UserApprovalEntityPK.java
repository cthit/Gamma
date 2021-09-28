package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.adapter.secondary.jpa.client.ClientEntity;
import it.chalmers.gamma.app.domain.PKId;
import it.chalmers.gamma.app.domain.client.Client;
import it.chalmers.gamma.app.domain.Id;
import it.chalmers.gamma.app.domain.user.UserId;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
public class UserApprovalEntityPK extends PKId<UserApprovalEntityPK.UserApprovalPKDTO> {

    protected record UserApprovalPKDTO(UserId userId, Client client) {

    }
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private ClientEntity client;

    protected UserApprovalEntityPK() {}

    public UserApprovalEntityPK(UserEntity user, ClientEntity client) {
        this.user = user;
        this.client = client;
    }

    public UserEntity getUserEntity() {
        return this.user;
    }

    @Override
    public UserApprovalPKDTO getValue() {
        return new UserApprovalPKDTO(
                this.user.id(),
                this.client.toDomain()
        );
    }
}
