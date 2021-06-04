package it.chalmers.gamma.bootstrap;

import org.jboss.logging.Logger;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@DependsOn("mockBootstrap")
@Component
public class AuthorityBootstrap {

    private static final Logger LOGGER = Logger.getLogger(AuthorityBootstrap.class);

    @PostConstruct
    public void createAuthorities() {
        LOGGER.info("TODO: Implement AuthorityBootstrap");
    }

}
