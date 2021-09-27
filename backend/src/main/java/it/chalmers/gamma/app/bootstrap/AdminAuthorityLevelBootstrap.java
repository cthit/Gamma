package it.chalmers.gamma.app.bootstrap;

import it.chalmers.gamma.app.domain.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.app.facade.AuthorityLevelFacade;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class AdminAuthorityLevelBootstrap {

    private final AuthorityLevelFacade authorityLevelFacade;

    public AdminAuthorityLevelBootstrap(AuthorityLevelFacade authorityLevelFacade) {
        this.authorityLevelFacade = authorityLevelFacade;
    }

    public void ensureAdminAuthorityLevel() {
        String admin = "admin";
        this.authorityLevelFacade.create(admin);
    }

}
