package it.chalmers.gamma.app.datadeletion.domain;

import it.chalmers.gamma.app.common.Id;
import java.util.Objects;
import java.util.UUID;

public record DataDeletionRequestId(UUID value) implements Id<UUID> {

  public DataDeletionRequestId {
    Objects.requireNonNull(value);
  }

  public static DataDeletionRequestId generate() {
    return new DataDeletionRequestId(UUID.randomUUID());
  }

  @Override
  public UUID getValue() {
    return this.value;
  }
}
