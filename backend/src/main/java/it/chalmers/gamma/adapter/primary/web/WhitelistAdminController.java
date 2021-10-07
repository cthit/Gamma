package it.chalmers.gamma.adapter.primary.web;

import java.util.List;

import it.chalmers.gamma.app.facade.WhitelistFacade;
import it.chalmers.gamma.app.port.repository.WhitelistRepository;
import it.chalmers.gamma.util.response.ErrorResponse;
import it.chalmers.gamma.util.response.NotFoundResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/admin/users/whitelist")
public final class WhitelistAdminController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WhitelistAdminController.class);

    private final WhitelistFacade whitelistFacade;

    public WhitelistAdminController(WhitelistFacade whitelistFacade) {
        this.whitelistFacade = whitelistFacade;
    }

    @GetMapping("/{cid}/activated")
    public CidIsWhitelistedResponse getWhitelistItem(@PathVariable("cid") String cid) {
        if (!this.whitelistFacade.isWhitelisted(cid)) {
            throw new CidNotWhitelistedResponse();
        }
        return new CidIsWhitelistedResponse();
    }

    @GetMapping()
    public List<String> getWhiteList() {
        return this.whitelistFacade.getWhitelist();
    }

    private record AddToWhitelist(String cid) { }

    @PostMapping()
    public WhitelistAddedResponse addWhitelistedUser(@RequestBody AddToWhitelist request) {
        try {
            this.whitelistFacade.whitelist(request.cid);
        } catch (WhitelistRepository.AlreadyWhitelistedException e) {
            throw new CidAlreadyWhitelistedResponse();
        }

        return new WhitelistAddedResponse();
    }

    @DeleteMapping("/{cid}")
    public CidRemovedFromWhitelistResponse removeWhitelist(@PathVariable("cid") String cid) {
        try {
            this.whitelistFacade.removeFromWhitelist(cid);
        } catch (WhitelistRepository.CidIsNotWhitelistedException e) {
            throw new CidNotWhitelistedResponse();
        }
        return new CidRemovedFromWhitelistResponse();
    }

    private static class CidRemovedFromWhitelistResponse extends SuccessResponse { }

    private static class WhitelistAddedResponse extends SuccessResponse { }

    private static class CidNotWhitelistedResponse extends NotFoundResponse { }

    private static class CidAlreadyWhitelistedResponse extends ErrorResponse {
        private CidAlreadyWhitelistedResponse() {
            super(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    private static class CidIsWhitelistedResponse extends SuccessResponse { }

}
