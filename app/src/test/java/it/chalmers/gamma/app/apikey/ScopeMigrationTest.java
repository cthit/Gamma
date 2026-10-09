package it.chalmers.gamma.app.apikey;

import static org.assertj.core.api.Assertions.assertThat;

import it.chalmers.gamma.app.apikey.domain.Scope;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

/**
 * The scope values written by the migration must be the {@link Scope} enum names — anything else
 * fails when JPA reads {@code @Enumerated(EnumType.STRING)} columns.
 */
class ScopeMigrationTest {

  @Test
  void migrationWritesEnumNames() throws IOException {
    String sql = readMigration();

    for (Scope scope : Scope.values()) {
      assertThat(sql).as("scope %s", scope.name()).contains("'" + scope.name() + "'");
    }
    assertThat(sql)
        .as("no oauth-style scope values such as 'profiles:read'")
        .doesNotContainPattern("'[a-z][a-z-]*:[a-z]+'");
  }

  private String readMigration() throws IOException {
    try (InputStream in = getClass().getResourceAsStream("/db/migration/V6__API_KEY_SCOPES.sql")) {
      assertThat(in).isNotNull();
      return new String(in.readAllBytes(), StandardCharsets.UTF_8);
    }
  }
}
