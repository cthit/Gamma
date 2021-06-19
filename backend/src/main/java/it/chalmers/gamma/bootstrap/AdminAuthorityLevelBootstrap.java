package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.domain.AuthorityLevelName;
import it.chalmers.gamma.internal.authoritylevel.service.AuthorityLevelService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class AdminAuthorityLevelBootstrap {

    private final AuthorityLevelService authorityLevelService;

    public AdminAuthorityLevelBootstrap(AuthorityLevelService authorityLevelService) {
        this.authorityLevelService = authorityLevelService;
    }

    @PostConstruct
    public void ensureAdminAuthorityLevel() {
        String admin = "admin";
        AuthorityLevelName adminAuthorityLevel = AuthorityLevelName.valueOf(admin);

        try {
            this.authorityLevelService.create(adminAuthorityLevel);
        } catch (AuthorityLevelService.AuthorityLevelAlreadyExistsException e) {
            // admin authorityLevelName already exists, moving on
        }
    }

}
