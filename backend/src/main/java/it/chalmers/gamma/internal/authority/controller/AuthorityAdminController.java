package it.chalmers.gamma.internal.authority.controller;

import it.chalmers.gamma.domain.Authorities;
import it.chalmers.gamma.domain.AuthorityLevelName;
import it.chalmers.gamma.internal.authority.service.AuthorityFinder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/internal/admin/authority")
public class AuthorityAdminController {

    private final AuthorityFinder authorityFinder;

    public AuthorityAdminController(AuthorityFinder authorityFinder) {
        this.authorityFinder = authorityFinder;
    }

    @GetMapping
    public List<Authorities> getAuthorities() {
        return this.authorityFinder.getAuthorities();
    }

}
