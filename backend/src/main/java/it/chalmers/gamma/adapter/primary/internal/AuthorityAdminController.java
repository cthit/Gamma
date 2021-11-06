package it.chalmers.gamma.adapter.primary.internal;

import it.chalmers.gamma.app.facade.internal.AuthorityLevelFacade;
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
    public List<AuthorityLevelFacade.AuthorityLevelDTO> getAuthorities() {
        return this.authorityLevelFacade.getAll();
    }

}
