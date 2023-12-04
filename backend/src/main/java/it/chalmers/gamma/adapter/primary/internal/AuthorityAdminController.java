package it.chalmers.gamma.adapter.primary.internal;

import it.chalmers.gamma.app.client.domain.ClientAuthorityFacade;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/admin/authority")
public final class AuthorityAdminController {

    private final ClientAuthorityFacade clientAuthorityFacade;

    public AuthorityAdminController(ClientAuthorityFacade clientAuthorityFacade) {
        this.clientAuthorityFacade = clientAuthorityFacade;
    }

}
