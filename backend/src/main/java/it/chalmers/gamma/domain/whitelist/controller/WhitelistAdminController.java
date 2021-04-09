package it.chalmers.gamma.domain.whitelist.controller;

import it.chalmers.gamma.util.domain.Cid;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityAlreadyExistsException;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.domain.user.service.UserFinder;
import it.chalmers.gamma.domain.whitelist.service.WhitelistFinder;
import it.chalmers.gamma.domain.whitelist.service.WhitelistService;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;

import it.chalmers.gamma.util.ResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public WhitelistAdminController(WhitelistFinder whitelistFinder,
                                    WhitelistService whitelistService) {
        this.whitelistFinder = whitelistFinder;
        this.whitelistService = whitelistService;
    }

    @GetMapping()
    public GetAllWhitelistResponse getWhiteList() {
        return  new GetAllWhitelistResponse(this.whitelistFinder.getAll());
    }

    @PostMapping()
    public ResponseEntity<?> addWhitelistedUsers(@Valid @RequestBody AddListOfWhitelistedRequest request) {
        List<Cid> failedToAdd = new ArrayList<>();

        for (Cid cid : request.cids) {
            try {
                this.whitelistService.create(cid);
                LOGGER.info("Added user " + cid + " to whitelist");
            } catch (EntityAlreadyExistsException e) {
                LOGGER.info("Failed to add " + cid + " to whitelist");
                failedToAdd.add(cid);
            }
        }

        if (!failedToAdd.isEmpty()) {
            return new ResponseEntity<>(new SomeAddedToWhitelistResponse(failedToAdd), HttpStatus.PARTIAL_CONTENT);
        }

        return new WhitelistAddedResponse();
    }

    @DeleteMapping("/{cid}")
    public CidRemovedFromWhitelistResponse removeWhitelist(@PathVariable("cid") Cid cid) {
        try {
            this.whitelistService.delete(cid);
        } catch (EntityNotFoundException e) {
            throw new CidNotWhitelistedResponse();
        }
        return new CidRemovedFromWhitelistResponse();
    }

}
