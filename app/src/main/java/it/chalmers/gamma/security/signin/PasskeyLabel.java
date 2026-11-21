package it.chalmers.gamma.security.signin;

public final class PasskeyLabel {

  public static final int MAX_LENGTH = 60;

  private PasskeyLabel() {}

  public static boolean isValid(String label) {
    return label != null && !label.isBlank() && label.length() <= MAX_LENGTH;
  }
}
