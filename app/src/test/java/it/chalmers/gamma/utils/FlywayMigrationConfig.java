package it.chalmers.gamma.utils;

import java.sql.Connection;
import java.sql.SQLException;
import org.flywaydb.core.Flyway;

public class FlywayMigrationConfig {

  /**
   * Called by Testcontainer. Check application-test.yml, there you can see that this function is
   * specified
   */
  public static void initFlywayFromTC(Connection connection) throws SQLException {
    Flyway flyway =
        Flyway.configure()
            .dataSource(
                connection.getMetaData().getURL(), connection.getMetaData().getUserName(), "test")
            .baselineOnMigrate(true)
            .locations("classpath:/db/migration")
            .load();
    flyway.migrate();
  }
}
