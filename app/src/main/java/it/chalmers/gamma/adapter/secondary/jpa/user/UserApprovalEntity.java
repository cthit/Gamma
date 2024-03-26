package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.adapter.secondary.jpa.client.ClientEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "g_user_approval")
public class UserApprovalEntity extends ImmutableEntity<UserApprovalEntityPK> {

  @EmbeddedId private UserApprovalEntityPK id;

  public UserApprovalEntity() {}

  public UserApprovalEntity(UserEntity user, ClientEntity client) {
    this.id = new UserApprovalEntityPK(user, client);
  }

  @Override
  public UserApprovalEntityPK getId() {
    return this.id;
  }

  public UserEntity getUserEntity() {
    return this.id.getUserEntity();
  }

  public ClientEntity getClientEntity() {
    return this.id.getClientEntity();
  }
}
