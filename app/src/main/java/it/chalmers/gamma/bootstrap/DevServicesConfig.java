package it.chalmers.gamma.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.data.redis.autoconfigure.DataRedisConnectionDetails;
import org.springframework.boot.jdbc.autoconfigure.JdbcConnectionDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(
    name = {
      "org.testcontainers.containers.PostgreSQLContainer",
      "org.testcontainers.containers.GenericContainer"
    })
@ConditionalOnProperty(
    name = "application.production",
    havingValue = "false",
    matchIfMissing = true)
@ConditionalOnProperty(
    name = "gamma.dev-services.enabled",
    havingValue = "true",
    matchIfMissing = true)
public class DevServicesConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(DevServicesConfig.class);
  private static final DockerImageName POSTGRES_IMAGE = DockerImageName.parse("postgres:16");
  private static final DockerImageName REDIS_IMAGE = DockerImageName.parse("redis:7-alpine");
  private static final int REDIS_PORT = 6379;

  @Bean(initMethod = "start", destroyMethod = "stop")
  public PostgreSQLContainer<?> postgresContainer() {
    return new PostgreSQLContainer<>(POSTGRES_IMAGE)
        .withDatabaseName("postgres")
        .withUsername("postgres")
        .withPassword("postgres");
  }

  @Bean(initMethod = "start", destroyMethod = "stop")
  public GenericContainer<?> redisContainer() {
    return new GenericContainer<>(REDIS_IMAGE).withExposedPorts(REDIS_PORT);
  }

  @Bean
  public JdbcConnectionDetails jdbcConnectionDetails(PostgreSQLContainer<?> postgresContainer) {
    LOGGER.info("Development PostgreSQL container started at {}", postgresContainer.getJdbcUrl());
    return new JdbcConnectionDetails() {
      @Override
      public String getUsername() {
        return postgresContainer.getUsername();
      }

      @Override
      public String getPassword() {
        return postgresContainer.getPassword();
      }

      @Override
      public String getJdbcUrl() {
        return postgresContainer.getJdbcUrl();
      }
    };
  }

  @Bean
  public DataRedisConnectionDetails dataRedisConnectionDetails(GenericContainer<?> redisContainer) {
    LOGGER.info(
        "Development Redis container started at {}:{}",
        redisContainer.getHost(),
        redisContainer.getMappedPort(REDIS_PORT));
    return new DataRedisConnectionDetails() {
      @Override
      public Standalone getStandalone() {
        return Standalone.of(redisContainer.getHost(), redisContainer.getMappedPort(REDIS_PORT));
      }
    };
  }
}
