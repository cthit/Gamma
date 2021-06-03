package it.chalmers.gamma.internal.authority.controller;

import it.chalmers.gamma.domain.Authorities;
import it.chalmers.gamma.internal.authority.level.service.AuthorityLevelName;
import it.chalmers.gamma.internal.authority.service.AuthorityFinder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/internal/admin/authority")
public class AuthorityAdminController {

    private final AuthorityFinder authorityFinder;

    public AuthorityAdminController(AuthorityFinder authorityFinder) {
        this.authorityFinder = authorityFinder;
    }

    @GetMapping
    public Map<AuthorityLevelName, Authorities> getAuthorities() {
        return this.authorityFinder.getAuthorities();
    }

}
