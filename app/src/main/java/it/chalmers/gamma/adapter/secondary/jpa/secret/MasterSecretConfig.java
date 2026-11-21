package it.chalmers.gamma.adapter.secondary.jpa.secret;

import java.security.SecureRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Loads the master secret from the database, generating it on first start. Keeping it in the
 * database means it survives restarts and moves with database backups.
 */
@Configuration
public class MasterSecretConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(MasterSecretConfig.class);

  private static final String NAME = "master";

  @Bean
  public MasterSecret masterSecret(JdbcTemplate jdbc) {
    byte[] generated = new byte[MasterSecret.BYTES];
    new SecureRandom().nextBytes(generated);
    int inserted =
        jdbc.update(
            "INSERT INTO g_application_secret (name, value) VALUES (?, ?)"
                + " ON CONFLICT (name) DO NOTHING",
            NAME,
            generated);
    if (inserted == 1) {
      LOGGER.info("Generated a new master secret in g_application_secret");
    }
    byte[] stored =
        jdbc.queryForObject(
            "SELECT value FROM g_application_secret WHERE name = ?", byte[].class, NAME);
    return new MasterSecret(stored);
  }
}
