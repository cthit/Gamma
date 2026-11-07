package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import it.chalmers.gamma.app.user.activation.domain.UserActivation;
import it.chalmers.gamma.app.user.activation.domain.UserActivationToken;
import it.chalmers.gamma.app.user.domain.Cid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "g_user_activation")
public class UserActivationEntity extends ImmutableEntity<String> {

  @Id
  @Column(name = "cid")
  private String cid;

  /** SHA-256 hash of the activation token, the raw token is never stored. */
  @Column(name = "token")
  private String tokenHash;

  protected UserActivationEntity() {}

  protected UserActivationEntity(Cid cid, UserActivationToken token) {
    this.cid = cid.getValue();
    this.tokenHash = token.hashed();
  }

  public UserActivation toDomain() {
    return new UserActivation(Cid.valueOf(this.cid), this.getCreatedAt());
  }

  @Override
  public String getId() {
    return this.cid;
  }

  public Cid cid() {
    return Cid.valueOf(this.cid);
  }
}
