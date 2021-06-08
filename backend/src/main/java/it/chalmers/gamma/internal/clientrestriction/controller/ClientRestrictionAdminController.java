package it.chalmers.gamma.internal.clientrestriction.controller;

import it.chalmers.gamma.domain.AuthorityLevelName;
import it.chalmers.gamma.domain.ClientRestrictions;
import it.chalmers.gamma.internal.clientrestriction.service.ClientRestrictionPK;
import it.chalmers.gamma.internal.clientrestriction.service.ClientRestrictionService;
import it.chalmers.gamma.domain.ClientId;
import it.chalmers.gamma.util.response.ErrorResponse;
import it.chalmers.gamma.util.response.NotFoundResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/admin/clients/restriction")
public class ClientRestrictionAdminController {

    private final ClientRestrictionService clientRestrictionService;

    public ClientRestrictionAdminController(ClientRestrictionService clientRestrictionService) {
        this.clientRestrictionService = clientRestrictionService;
    }

    @GetMapping("/{clientId}")
    public ClientRestrictions getClientRestrictions(@PathVariable("clientId") ClientId clientId) {
        try {
            return this.clientRestrictionService.get(clientId);
        } catch (ClientRestrictionService.ClientRestrictionNotFoundException e) {
            throw new ClientNotFoundResponse();
        }
    }

    @DeleteMapping("/{clientId}")
    public ClientRestrictionDeleted deleteClientRestriction(@PathVariable("clientId") ClientId clientId,
                                                            @RequestParam("authorityLevelName") AuthorityLevelName authorityLevelName) {
        this.clientRestrictionService.delete(
                new ClientRestrictionPK(
                        clientId,
                        authorityLevelName
                )
        );
        return new ClientRestrictionDeleted();
    }

    private static class ClientRestrictionDeleted extends SuccessResponse { }

    private static class ClientNotFoundResponse extends NotFoundResponse { }

}
