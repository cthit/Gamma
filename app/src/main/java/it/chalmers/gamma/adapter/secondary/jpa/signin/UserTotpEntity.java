package it.chalmers.gamma.adapter.secondary.jpa.signin;

import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "g_user_totp")
public class UserTotpEntity extends ImmutableEntity<UUID> {

  @Id
  @Column(name = "user_id", columnDefinition = "uuid")
  protected UUID userId;

  @Column(name = "secret")
  protected String secret;

  @Column(name = "last_used_time_step")
  protected long lastUsedTimeStep;

  protected UserTotpEntity() {}

  UserTotpEntity(UUID userId, String secret, long lastUsedTimeStep) {
    this.userId = userId;
    this.secret = secret;
    this.lastUsedTimeStep = lastUsedTimeStep;
  }

  @Override
  public UUID getId() {
    return this.userId;
  }
}
