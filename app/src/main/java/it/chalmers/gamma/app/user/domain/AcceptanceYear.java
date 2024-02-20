package it.chalmers.gamma.app.user.domain;

import java.io.Serializable;

public record AcceptanceYear(int value) implements Serializable {

  public AcceptanceYear {
    if (value < 2001) {
      throw new IllegalArgumentException("Acceptance year cannot be less than 2001");
    }
  }
}
