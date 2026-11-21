package it.chalmers.gamma.security.signin;

import org.springframework.security.core.AuthenticationException;

public class TooManyTotpCodesException extends AuthenticationException {

  public TooManyTotpCodesException() {
    super("Too many wrong authenticator app codes");
  }
}
