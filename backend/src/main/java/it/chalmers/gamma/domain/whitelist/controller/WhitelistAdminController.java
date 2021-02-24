package it.chalmers.gamma.domain.whitelist.controller;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.EntityAlreadyExistsException;
import it.chalmers.gamma.domain.EntityNotFoundException;
import it.chalmers.gamma.domain.user.service.UserFinder;
import it.chalmers.gamma.domain.whitelist.exception.CidAlreadyWhitelistedException;
import it.chalmers.gamma.domain.whitelist.exception.CidNotWhitelistedException;
import it.chalmers.gamma.domain.whitelist.controller.response.*;
import it.chalmers.gamma.domain.whitelist.service.WhitelistFinder;
import it.chalmers.gamma.domain.whitelist.service.WhitelistService;
import it.chalmers.gamma.domain.whitelist.controller.request.AddListOfWhitelistedRequest;

import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.domain.user.controller.response.UserDeletedResponse;

import it.chalmers.gamma.util.InputValidationUtils;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;

import it.chalmers.gamma.util.Utils;
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
                this.whitelistService.create(new Cid(cid));
                LOGGER.info("Added user " + cid + " to whitelist");
            } catch (EntityAlreadyExistsException e) {
                failedToAdd.add(cid);
            }
        }

        if (!failedToAdd.isEmpty()) {
            return new SomeAddedToWhitelistResponse(failedToAdd).toResponseObject();
        }

        return new WhitelistAddedResponse();
    }

    @DeleteMapping("/{cid}")
    public UserDeletedResponse removeWhitelist(@PathVariable("cid") Cid cid) {
        try {
            this.whitelistService.delete(cid);
        } catch (EntityNotFoundException e) {
            throw new CidNotWhitelistedResponse();
        }
        return new UserDeletedResponse();
    }

    @GetMapping()
    public ResponseEntity<GetAllWhitelistResponse> getWhiteList() {
        return Utils.toResponseObject(
                new GetAllWhitelistResponse(this.whitelistFinder.getAll())
        );
    }

}
