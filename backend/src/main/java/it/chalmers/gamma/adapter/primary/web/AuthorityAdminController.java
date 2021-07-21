package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.AuthorityFacade;
import it.chalmers.gamma.app.domain.AuthorityLevel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internal/admin/authority")
public final class AuthorityAdminController {

    private final AuthorityFacade authorityFacade;

    public AuthorityAdminController(AuthorityFacade authorityFacade) {
        this.authorityFacade = authorityFacade;
    }

    @GetMapping
    public List<AuthorityLevel> getAuthorities() {
        return null;
//        return this.authorityFinder.getAuthorities();
    }

}
