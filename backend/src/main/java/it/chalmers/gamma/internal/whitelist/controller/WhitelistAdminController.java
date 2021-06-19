package it.chalmers.gamma.internal.whitelist.controller;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.internal.whitelist.service.WhitelistService;

import java.util.List;
import javax.validation.Valid;

import it.chalmers.gamma.util.response.ErrorResponse;
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

    private final WhitelistService whitelistService;

    public WhitelistAdminController(WhitelistService whitelistService) {
        this.whitelistService = whitelistService;
    }

    @GetMapping()
    public List<Cid> getWhiteList() {
        return this.whitelistService.getAll();
    }

    private record AddToWhitelist(Cid cid) { }

    @PostMapping()
    public WhitelistAddedResponse addWhitelistedUser(@Valid @RequestBody AddToWhitelist request) {
        try {
            this.whitelistService.create(request.cid);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CidAlreadyWhitelistedResponse();
        }

        return new WhitelistAddedResponse();
    }

    @DeleteMapping("/{cid}")
    public CidRemovedFromWhitelistResponse removeWhitelist(@PathVariable("cid") Cid cid) {
        try {
            this.whitelistService.delete(cid);
        } catch (WhitelistService.WhitelistNotFoundException e) {
            throw new CidNotWhitelistedResponse();
        }
        return new CidRemovedFromWhitelistResponse();
    }

    private static class CidRemovedFromWhitelistResponse extends SuccessResponse { }

    private static class WhitelistAddedResponse extends SuccessResponse { }

    private static class CidNotWhitelistedResponse extends ErrorResponse {
        private CidNotWhitelistedResponse() {
            super(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    private static class CidAlreadyWhitelistedResponse extends ErrorResponse {
        private CidAlreadyWhitelistedResponse() {
            super(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }


}
