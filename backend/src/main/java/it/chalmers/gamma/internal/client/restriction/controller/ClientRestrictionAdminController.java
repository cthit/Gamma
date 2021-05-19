package it.chalmers.gamma.internal.client.restriction.controller;

import it.chalmers.gamma.internal.authority.level.service.AuthorityLevelName;
import it.chalmers.gamma.internal.client.restriction.service.ClientRestrictionDTO;
import it.chalmers.gamma.internal.client.restriction.service.ClientRestrictionFinder;
import it.chalmers.gamma.internal.client.restriction.service.ClientRestrictionPK;
import it.chalmers.gamma.internal.client.restriction.service.ClientRestrictionService;
import it.chalmers.gamma.internal.client.service.ClientId;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.util.response.ErrorResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/clients/restriction")
public class ClientRestrictionAdminController {

    private final ClientRestrictionFinder clientRestrictionFinder;
    private final ClientRestrictionService clientRestrictionService;

    public ClientRestrictionAdminController(ClientRestrictionFinder clientRestrictionFinder,
                                            ClientRestrictionService clientRestrictionService) {
        this.clientRestrictionFinder = clientRestrictionFinder;
        this.clientRestrictionService = clientRestrictionService;
    }

    @GetMapping("/{clientId}")
    public ClientRestrictionDTO getClientRestrictions(@PathVariable("clientId") ClientId clientId) {
        try {
            return this.clientRestrictionFinder.get(clientId);
        } catch (EntityNotFoundException e) {
            throw new ClientNotFoundResponse();
        }
    }

    @DeleteMapping("/{clientId}")
    public ClientRestrictionDeleted deleteClientRestriction(@PathVariable("clientId") ClientId clientId,
                                                            @RequestParam("authorityLevelName") AuthorityLevelName authorityLevelName) {
        try {
            this.clientRestrictionService.delete(
                    new ClientRestrictionPK(
                            clientId,
                            authorityLevelName
                    )
            );
            return new ClientRestrictionDeleted();
        } catch (EntityNotFoundException e) {
            throw new ClientNotFoundResponse();
        }
    }

    private static class ClientRestrictionDeleted extends SuccessResponse { }

    private static class ClientNotFoundResponse extends ErrorResponse {
        private ClientNotFoundResponse() {
            super(HttpStatus.NOT_FOUND);
        }
    }

}
