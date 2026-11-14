package it.chalmers.gamma.app.signin;

import it.chalmers.gamma.app.user.domain.UserId;

/** A notification is sent only after the credential change commits. Never contains credentials. */
public record SignInChanged(UserId userId, String description) {}
