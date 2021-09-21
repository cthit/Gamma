package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.app.authoritylevel.AuthorityLevelFacade;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class AdminAuthorityLevelBootstrap {

    private final AuthorityLevelFacade authorityLevelFacade;

    public AdminAuthorityLevelBootstrap(AuthorityLevelFacade authorityLevelFacade) {
        this.authorityLevelFacade = authorityLevelFacade;
    }

    @PostConstruct
    public void ensureAdminAuthorityLevel() {
//        String admin = "admin";
//        AuthorityLevelName adminAuthorityLevel = AuthorityLevelName.valueOf(admin);
//
//        try {
//            this.authorityLevelService.create(adminAuthorityLevel);
//        } catch (AuthorityLevelService.AuthorityLevelAlreadyExistsException e) {
//            // admin name already exists, moving on
//        }
    }

}
