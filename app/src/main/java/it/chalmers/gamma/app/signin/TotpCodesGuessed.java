package it.chalmers.gamma.app.signin;

import it.chalmers.gamma.app.user.domain.UserId;

/** Someone who knows the password has entered too many wrong authenticator app codes. */
public record TotpCodesGuessed(UserId userId) {}
