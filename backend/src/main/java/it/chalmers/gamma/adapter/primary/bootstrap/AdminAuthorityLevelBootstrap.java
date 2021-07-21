package it.chalmers.gamma.adapter.primary.bootstrap;

import it.chalmers.gamma.app.AuthorityFacade;
import it.chalmers.gamma.app.domain.AuthorityLevelName;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class AdminAuthorityLevelBootstrap {

    private final AuthorityFacade authorityFacade;

    public AdminAuthorityLevelBootstrap(AuthorityFacade authorityFacade) {
        this.authorityFacade = authorityFacade;
    }

    @PostConstruct
    public void ensureAdminAuthorityLevel() {
        String admin = "admin";
        AuthorityLevelName adminAuthorityLevel = AuthorityLevelName.valueOf(admin);

        try {
            this.authorityLevelService.create(adminAuthorityLevel);
        } catch (AuthorityLevelService.AuthorityLevelAlreadyExistsException e) {
            // admin name already exists, moving on
        }
    }

}
