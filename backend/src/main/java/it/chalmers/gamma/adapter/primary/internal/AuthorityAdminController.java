package it.chalmers.gamma.adapter.primary.internal;

import it.chalmers.gamma.app.authority.ClientAuthorityFacade;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internal/admin/authority")
public final class AuthorityAdminController {

    private final ClientAuthorityFacade clientAuthorityFacade;

    public AuthorityAdminController(ClientAuthorityFacade clientAuthorityFacade) {
        this.clientAuthorityFacade = clientAuthorityFacade;
    }

}
