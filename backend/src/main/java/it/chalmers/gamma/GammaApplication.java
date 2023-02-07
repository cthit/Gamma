package it.chalmers.gamma;

import it.chalmers.gamma.util.controller.ErrorHandlingControllerAdvice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@ConfigurationPropertiesScan
@Import(ErrorHandlingControllerAdvice.class)
public class GammaApplication {

    public static void main(String[] args) {
        SpringApplication.run(GammaApplication.class, args);
    }

}
