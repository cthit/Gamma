package it.chalmers.gamma;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;

@SpringBootApplication
public class GammaApplication {

    public static void main(String[] args) {
        SpringApplication.run(GammaApplication.class, args);
    }

}
