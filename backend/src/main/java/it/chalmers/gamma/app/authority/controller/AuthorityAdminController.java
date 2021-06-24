package it.chalmers.gamma.app.authority.controller;

import it.chalmers.gamma.app.domain.Authorities;
import it.chalmers.gamma.app.authority.service.AuthorityFinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
