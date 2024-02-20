package it.chalmers.gamma.app.group.domain;

import it.chalmers.gamma.app.common.Id;
import java.util.Objects;
import java.util.UUID;

public record GroupId(UUID value) implements Id<UUID> {

  public GroupId {
    Objects.requireNonNull(value);
  }

  public static GroupId generate() {
    return new GroupId(UUID.randomUUID());
  }

  public static GroupId valueOf(String value) {
    return new GroupId(UUID.fromString(value));
  }

  @Override
  public UUID getValue() {
    return this.value;
  }
}
