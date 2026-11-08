package it.chalmers.gamma.adapter.secondary.jpa.signin;

import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "g_user_recovery_key")
public class UserRecoveryKeyEntity extends ImmutableEntity<UUID> {

  @Id
  @Column(name = "user_id", columnDefinition = "uuid")
  protected UUID userId;

  @Column(name = "key_hash")
  protected String keyHash;

  protected UserRecoveryKeyEntity() {}

  UserRecoveryKeyEntity(UUID userId, String keyHash) {
    this.userId = userId;
    this.keyHash = keyHash;
  }

  @Override
  public UUID getId() {
    return this.userId;
  }

  Instant createdAt() {
    return getCreatedAt();
  }
}
