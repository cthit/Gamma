package it.chalmers.gamma.whitelist.controller;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.user.service.UserFinder;
import it.chalmers.gamma.whitelist.exception.CidAlreadyWhitelistedException;
import it.chalmers.gamma.whitelist.exception.CidNotWhitelistedException;
import it.chalmers.gamma.whitelist.exception.UserAlreadyExistsWithCidException;
import it.chalmers.gamma.whitelist.response.*;
import it.chalmers.gamma.whitelist.service.WhitelistFinder;
import it.chalmers.gamma.whitelist.service.WhitelistService;
import it.chalmers.gamma.whitelist.request.AddListOfWhitelistedRequest;

import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.user.controller.response.UserAlreadyExistsResponse;
import it.chalmers.gamma.user.controller.response.UserDeletedResponse;
import it.chalmers.gamma.whitelist.response.GetAllWhitelistResponse.GetAllWhitelistResponseObject;

import it.chalmers.gamma.util.InputValidationUtils;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/users/whitelist")
public final class WhitelistAdminController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WhitelistAdminController.class);

    private final WhitelistFinder whitelistFinder;
    private final WhitelistService whitelistService;
    private final UserFinder userFinder;

    public WhitelistAdminController(WhitelistFinder whitelistFinder,
                                    WhitelistService whitelistService,
                                    UserFinder userFinder) {
        this.whitelistFinder = whitelistFinder;
        this.whitelistService = whitelistService;
        this.userFinder = userFinder;
    }

    @PostMapping()
    public ResponseEntity<?> addWhitelistedUsers(
            @Valid @RequestBody AddListOfWhitelistedRequest request, BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }

        List<String> failedToAdd = new ArrayList<>();

        for (String cid : request.getCids()) {
            try {
                this.whitelistService.addWhiteListedCid(new Cid(cid));
                LOGGER.info("Added user " + cid + " to whitelist");
            } catch (CidAlreadyWhitelistedException | UserAlreadyExistsWithCidException e) {
                LOGGER.info("Did not add user " + cid + " message: " + e.getMessage());
                failedToAdd.add(cid);
            }
        }

        if (!failedToAdd.isEmpty()) {
            return new SomeAddedToWhitelistResponse(failedToAdd).toResponseObject();
        }

        return new WhitelistAddedResponse();
    }

    @DeleteMapping("/{cid}")
    public UserDeletedResponse removeWhitelist(@PathVariable("cid") String cid) {
        try {
            this.whitelistService.removeWhiteListedCid(new Cid(cid));
        } catch (CidNotWhitelistedException e) {
            LOGGER.error("Can't remove cid from whitelist that doesn't exist", e);
            throw new CidNotWhitelistedResponse();
        }
        return new UserDeletedResponse();
    }

    @GetMapping()
    public GetAllWhitelistResponseObject getWhiteList() {
        return new GetAllWhitelistResponse(this.whitelistFinder.getWhitelist()).toResponseObject();
    }

}
