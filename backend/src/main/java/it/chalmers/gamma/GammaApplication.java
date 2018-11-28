package it.chalmers.gamma;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public final class GammaApplication {

    private GammaApplication() {}

    public static void main(String[] args) {
        SpringApplication.run(GammaApplication.class, args);
    }
}
