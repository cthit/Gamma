package it.chalmers.gamma.adapter.secondary.jpa.secret;

import java.security.SecureRandom;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Loads the master secret from the database, generating it on first start. Every instance sharing
 * the database reads the same secret, and it moves with database backups.
 */
@Configuration
public class MasterSecretConfig {

  private static final String NAME = "master";

  @Bean
  public MasterSecret masterSecret(JdbcTemplate jdbc) {
    byte[] generated = new byte[MasterSecret.BYTES];
    new SecureRandom().nextBytes(generated);
    // Instances starting at once all keep whichever secret was inserted first.
    jdbc.update(
        "INSERT INTO g_application_secret (name, value) VALUES (?, ?) ON CONFLICT (name) DO NOTHING",
        NAME,
        generated);
    byte[] stored =
        jdbc.queryForObject(
            "SELECT value FROM g_application_secret WHERE name = ?", byte[].class, NAME);
    return new MasterSecret(stored);
  }
}
