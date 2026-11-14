package it.chalmers.gamma.app.user;

import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.user.domain.UserId;

/**
 * An admin changed the user's email. The previous and the new address are notified after the change
 * commits. Never contains credentials.
 */
public record EmailChangedByAdmin(UserId userId, Email previousEmail) {}
