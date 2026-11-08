package it.chalmers.gamma.app.signin.domain;

import java.time.Instant;
import java.util.Objects;

/**
 * A passkey a user can sign in with.
 *
 * @param credentialId the base64url encoded credential id
 * @param label the name the user gave the passkey
 * @param created when the passkey was registered, if known
 * @param lastUsed when the passkey was last used to sign in, if ever
 */
public record Passkey(String credentialId, String label, Instant created, Instant lastUsed) {
  public Passkey {
    Objects.requireNonNull(credentialId);
    Objects.requireNonNull(label);
  }
}
