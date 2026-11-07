package it.chalmers.gamma;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@ConfigurationPropertiesScan
@EnableJpaRepositories("it.chalmers.gamma.adapter.secondary.jpa")
@EnableRedisRepositories("it.chalmers.gamma.adapter.secondary.redis")
public class GammaApplication {

  public static void main(String[] args) {
    SpringApplication.run(GammaApplication.class, args);
  }
}
