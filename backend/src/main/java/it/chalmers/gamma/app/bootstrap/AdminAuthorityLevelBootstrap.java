package it.chalmers.gamma.app.bootstrap;

import it.chalmers.gamma.app.domain.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.app.repository.AuthorityLevelRepository;
import org.springframework.stereotype.Component;

@Component
public class AdminAuthorityLevelBootstrap {

    private final AuthorityLevelRepository authorityLevelRepository;

    public AdminAuthorityLevelBootstrap(AuthorityLevelRepository authorityLevelRepository) {
        this.authorityLevelRepository = authorityLevelRepository;
    }

    public void ensureAdminAuthorityLevel() {
        String admin = "admin";
        this.authorityLevelRepository.create(new AuthorityLevelName(admin));
    }

}
