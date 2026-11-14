package it.chalmers.gamma.adapter.secondary.jpa.datadeletion;

import it.chalmers.gamma.adapter.secondary.jpa.util.AbstractEntity;
import it.chalmers.gamma.app.client.domain.ClientUid;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.datadeletion.domain.DataDeletionReason;
import it.chalmers.gamma.app.datadeletion.domain.DataDeletionRequest;
import it.chalmers.gamma.app.datadeletion.domain.DataDeletionRequestId;
import it.chalmers.gamma.app.user.domain.Cid;
import it.chalmers.gamma.app.user.domain.UserId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "g_data_deletion_request")
public class DataDeletionRequestEntity extends AbstractEntity<UUID> {

  @Id
  @Column(name = "data_deletion_request_id", columnDefinition = "uuid")
  private UUID id;

  @Column(name = "user_id", columnDefinition = "uuid", nullable = false)
  private UUID userId;

  @Column(name = "client_uid", columnDefinition = "uuid", nullable = false)
  private UUID clientUid;

  @Column(name = "cid")
  private String cid;

  @Column(name = "client_name")
  private String clientName;

  @Enumerated(EnumType.STRING)
  @Column(name = "reason", nullable = false)
  private DataDeletionReason reason;

  @Column(name = "re_approved_at")
  private Instant reApprovedAt;

  @Column(name = "cleared_by", columnDefinition = "uuid")
  private UUID clearedBy;

  @Column(name = "cleared_at")
  private Instant clearedAt;

  protected DataDeletionRequestEntity() {}

  protected DataDeletionRequestEntity(
      UUID id,
      UUID userId,
      String cid,
      UUID clientUid,
      String clientName,
      DataDeletionReason reason) {
    this.id = id;
    this.userId = userId;
    this.cid = cid;
    this.clientUid = clientUid;
    this.clientName = clientName;
    this.reason = reason;
  }

  @Override
  public UUID getId() {
    return this.id;
  }

  protected DataDeletionRequest toDomain() {
    return new DataDeletionRequest(
        new DataDeletionRequestId(this.id),
        new UserId(this.userId),
        new ClientUid(this.clientUid),
        this.cid == null ? null : new Cid(this.cid),
        this.clientName == null ? null : new PrettyName(this.clientName),
        this.reason,
        getCreatedAt(),
        this.reApprovedAt,
        this.clearedBy == null ? null : new UserId(this.clearedBy),
        this.clearedAt);
  }
}
