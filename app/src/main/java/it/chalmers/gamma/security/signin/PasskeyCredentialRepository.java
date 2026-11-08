package it.chalmers.gamma.security.signin;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.Nullable;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.web.webauthn.api.Bytes;
import org.springframework.security.web.webauthn.api.CredentialRecord;
import org.springframework.security.web.webauthn.management.JdbcUserCredentialRepository;
import org.springframework.security.web.webauthn.management.UserCredentialRepository;

public class PasskeyCredentialRepository implements UserCredentialRepository {

  private static final String UPDATE_USAGE_SQL =
      "UPDATE user_credentials SET signature_count = ?, last_used = ? WHERE credential_id = ?";

  private final JdbcUserCredentialRepository delegate;
  private final JdbcOperations jdbcOperations;

  public PasskeyCredentialRepository(JdbcOperations jdbcOperations) {
    this.delegate = new JdbcUserCredentialRepository(jdbcOperations);
    this.jdbcOperations = jdbcOperations;
  }

  @Override
  public void save(CredentialRecord record) {
    if (Objects.equals(record.getCreated(), record.getLastUsed())) {
      this.delegate.save(record);
      return;
    }

    int updated =
        this.jdbcOperations.update(
            UPDATE_USAGE_SQL,
            record.getSignatureCount(),
            Timestamp.from(record.getLastUsed()),
            record.getCredentialId().toBase64UrlString());
    if (updated == 0) {
      throw new IllegalStateException("The passkey has been removed");
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
