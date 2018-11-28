package it.chalmers.gamma.config;

import it.chalmers.gamma.service.ITUserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "application.db-init", havingValue = "true")
public class DbInitializer implements CommandLineRunner {
    private ITUserService ITUserservice;

    private String schemaPath = "resources/schema.sql";      // TODO Add a dynamically set path instead.

    public DbInitializer(ITUserService ITUserService){
        this.ITUserservice = ITUserService;
    }
    @Override
    public void run(String... args) throws Exception {

    }
}
