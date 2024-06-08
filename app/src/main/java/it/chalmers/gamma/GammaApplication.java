package it.chalmers.gamma;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ConfigurationPropertiesScan
public class GammaApplication {

  public static void main(String[] args) {
    SpringApplication.run(GammaApplication.class, args);
  }
}
