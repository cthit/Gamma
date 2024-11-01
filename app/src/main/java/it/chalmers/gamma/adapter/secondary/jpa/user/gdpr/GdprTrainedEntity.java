package it.chalmers.gamma.adapter.secondary.jpa.user.gdpr;

import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "g_gdpr_trained")
public class GdprTrainedEntity extends ImmutableEntity<UUID> {

  @Id
  @Column(name = "user_id", columnDefinition = "uuid")
  private UUID userId;

  protected GdprTrainedEntity() {}

  public GdprTrainedEntity(UserEntity userEntity) {
    this.userId = userEntity.getId();
  }

  @Override
  public UUID getId() {
    return this.userId;
  }
}
