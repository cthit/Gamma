package it.chalmers.gamma.app.datadeletion.domain;

import it.chalmers.gamma.app.client.domain.ClientUid;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.user.domain.Cid;
import it.chalmers.gamma.app.user.domain.UserId;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;

public record DataDeletionRequest(
    DataDeletionRequestId id,
    UserId userId,
    ClientUid clientUid,
    Cid cid,
    PrettyName clientName,
    DataDeletionReason reason,
    Instant createdAt,
    Instant reApprovedAt,
    UserId clearedBy,
    Instant clearedAt) {

  public static final ZoneId DEADLINE_ZONE = ZoneId.of("Europe/Stockholm");

  public DataDeletionRequest {
    Objects.requireNonNull(id);
    Objects.requireNonNull(userId);
    Objects.requireNonNull(clientUid);
    Objects.requireNonNull(reason);
    Objects.requireNonNull(createdAt);

    if (clearedAt == null && (cid == null || clientName == null)) {
      throw new IllegalArgumentException("An open request must have a cid and a client name");
    }
  }

  public boolean isCleared() {
    return this.clearedAt != null;
  }

  public boolean isReApproved() {
    return this.reApprovedAt != null;
  }

  public LocalDate deadline() {
    return this.createdAt.atZone(DEADLINE_ZONE).toLocalDate().plusMonths(1);
  }

  public boolean isOverdue(Instant now) {
    return !isCleared() && now.atZone(DEADLINE_ZONE).toLocalDate().isAfter(deadline());
  }
}
