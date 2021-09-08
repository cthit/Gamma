package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.adapter.secondary.jpa.client.ClientEntity;
import it.chalmers.gamma.domain.client.Client;
import it.chalmers.gamma.domain.Id;
import it.chalmers.gamma.domain.user.User;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
public class UserApprovalEntityPK extends Id<UserApprovalEntityPK.UserApprovalPKDTO> implements Serializable {

    protected record UserApprovalPKDTO(User user, Client client) {
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

    @Override
    public UserApprovalPKDTO value() {
        return new UserApprovalPKDTO(
                this.user.toDomain(),
                this.client.toDomain()
        );
    }
}
