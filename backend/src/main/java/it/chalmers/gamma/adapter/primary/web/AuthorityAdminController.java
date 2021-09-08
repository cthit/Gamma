package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.AuthorityLevelFacade;
import it.chalmers.gamma.domain.authoritylevel.AuthorityLevel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internal/admin/authority")
public final class AuthorityAdminController {

    private final AuthorityLevelFacade authorityLevelFacade;

    public AuthorityAdminController(AuthorityLevelFacade authorityLevelFacade) {
        this.authorityLevelFacade = authorityLevelFacade;
    }

    @GetMapping
    public List<AuthorityLevel> getAuthorities() {
        return null;
//        return this.authorityFinder.getAuthorities();
    }

}
