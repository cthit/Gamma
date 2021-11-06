package it.chalmers.gamma;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;

@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
public class GammaApplication {

    public static void main(String[] args) {
        SpringApplication.run(GammaApplication.class, args);
    }

}
