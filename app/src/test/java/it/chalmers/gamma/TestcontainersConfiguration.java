package it.chalmers.gamma;

import com.redis.testcontainers.RedisContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

  @Bean
  @ServiceConnection
  PostgreSQLContainer postgreSQLContainer() {
    return new PostgreSQLContainer("postgres:16")
        .withDatabaseName("gamma-test")
        .withUsername("postgres-test")
        .withPassword("postgres-test");
  }

  @Bean
  @ServiceConnection
  RedisContainer redisContainer() {
    return new RedisContainer("redis:5.0").withExposedPorts(6379);
  }
}
