package it.chalmers.gamma.app.user.domain;

import it.chalmers.gamma.app.common.Id;
import java.io.Serializable;
import java.util.Locale;

public record Cid(String value) implements Id<String>, UserIdentifier, Serializable {

  public Cid {
    if (value == null) {
      throw new NullPointerException("Cid cannot be null");
    }

    value = value.toLowerCase(Locale.ROOT);

    if (!value.matches("^([a-z]{4,12})$")) {
      throw new IllegalArgumentException(
          "Cid length must be between 4 and 12, and only have letters between a - z");
    }
  }

  public static Cid valueOf(String cid) {
    return new Cid(cid);
  }

  public static boolean isValidCid(String possibleCid) {
    return possibleCid != null && possibleCid.matches("^([a-z]{4,12})$");
  }

  @Override
  public String getValue() {
    return this.value;
  }
}
