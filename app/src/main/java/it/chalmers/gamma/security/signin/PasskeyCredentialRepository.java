package it.chalmers.gamma.security.signin;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.webauthn.api.Bytes;
import org.springframework.security.web.webauthn.api.CredentialRecord;
import org.springframework.security.web.webauthn.management.JdbcUserCredentialRepository;
import org.springframework.security.web.webauthn.management.UserCredentialRepository;

public class PasskeyCredentialRepository implements UserCredentialRepository {

  /**
   * Spring Security's Webauthn4J operations compare the new signature counter only to the one in
   * the registration attestation, so the counter stored since is checked here. A counter that
   * doesn't increase means the passkey may have been cloned, unless the authenticator doesn't keep
   * one and always sends 0. Doing it in the update also keeps two sign-ins with the same counter
   * from both succeeding.
   */
  private static final String UPDATE_USAGE_SQL =
      "UPDATE user_credentials SET signature_count = ?, last_used = ?"
          + " WHERE credential_id = ? AND (COALESCE(signature_count, 0) < ?"
          + " OR (COALESCE(signature_count, 0) = 0 AND ? = 0))";

  private final JdbcUserCredentialRepository delegate;
  private final JdbcOperations jdbcOperations;
  private final ObjectProvider<PasskeyRegistrationGuard> registrationGuard;

  public PasskeyCredentialRepository(
      JdbcOperations jdbcOperations, ObjectProvider<PasskeyRegistrationGuard> registrationGuard) {
    this.delegate = new JdbcUserCredentialRepository(jdbcOperations);
    this.jdbcOperations = jdbcOperations;
    this.registrationGuard = registrationGuard;
  }

  @Override
  public void save(CredentialRecord record) {
    if (Objects.equals(record.getCreated(), record.getLastUsed())) {
      if (!PasskeyLabel.isValid(record.getLabel())) {
        throw new IllegalArgumentException("Invalid passkey label");
      }
      this.registrationGuard.getObject().register(() -> this.delegate.save(record));
      return;
    }

    int updated =
        this.jdbcOperations.update(
            UPDATE_USAGE_SQL,
            record.getSignatureCount(),
            Timestamp.from(record.getLastUsed()),
            record.getCredentialId().toBase64UrlString(),
            record.getSignatureCount(),
            record.getSignatureCount());
    if (updated == 0) {
      if (this.delegate.findByCredentialId(record.getCredentialId()) == null) {
        throw new IllegalStateException("The passkey has been removed");
      }
      throw new BadCredentialsException("The passkey's signature counter did not increase");
    }
  }

  @Override
  public void delete(Bytes credentialId) {
    this.delegate.delete(credentialId);
  }

  @Override
  public @Nullable CredentialRecord findByCredentialId(Bytes credentialId) {
    return this.delegate.findByCredentialId(credentialId);
  }

  @Override
  public List<CredentialRecord> findByUserId(Bytes userId) {
    return this.delegate.findByUserId(userId);
  }
}
