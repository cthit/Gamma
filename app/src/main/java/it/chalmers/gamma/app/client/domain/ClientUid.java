package it.chalmers.gamma.app.client.domain;

import it.chalmers.gamma.app.common.Id;
import java.util.Objects;
import java.util.UUID;

/** UUID value for Client. */
public record ClientUid(UUID value) implements Id<String> {

  public ClientUid {
    Objects.requireNonNull(value);
  }

  public static ClientUid generate() {
    return new ClientUid(UUID.randomUUID());
  }

  public static ClientUid valueOf(String uid) {
    return new ClientUid(UUID.fromString(uid));
  }

  @Override
  public String getValue() {
    return value.toString();
  }
}
