package it.chalmers.gamma.controller.admin;

import it.chalmers.gamma.db.entity.Whitelist;
import it.chalmers.gamma.requests.AddListOfWhitelistedRequest;
import it.chalmers.gamma.requests.WhitelistCodeRequest;
import it.chalmers.gamma.response.*;
import it.chalmers.gamma.service.ITUserService;
import it.chalmers.gamma.service.WhitelistService;

import java.util.List;
import java.util.UUID;

import it.chalmers.gamma.util.InputValidationUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
@RestController
@RequestMapping("/admin/users/whitelist")
public final class UsersWhitelistAdminController {

    private final WhitelistService whitelistService;
    private final ITUserService itUserService;

    public UsersWhitelistAdminController(WhitelistService whitelistService,
                                          ITUserService itUserService) {
        this.whitelistService = whitelistService;
        this.itUserService = itUserService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> addWhitelistedUsers(
            @Valid @RequestBody AddListOfWhitelistedRequest request, BindingResult result) {
        if (result.hasErrors()){
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        List<String> cids = request.getCids();
        for (String cid : cids) {
            if (this.whitelistService.isCIDWhiteListed(cid)) {
                throw new CIDAlreadyWhitelistedResponse();
            }
            if (this.itUserService.userExists(cid)) {
                throw new UserAlreadyExistsResponse();
            }
            this.whitelistService.addWhiteListedCID(cid);
        }
        return new WhitelistAddedResponse();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<String> editWhitelist(
            @RequestBody WhitelistCodeRequest request,
            @PathVariable("id") String id) {
        Whitelist oldWhitelist = this.whitelistService.getWhitelistById(id);
        if (oldWhitelist == null){
            throw new CidNotFoundResponse();
        }
        if (!this.whitelistService.isCIDWhiteListed(oldWhitelist.getCid())) {
            throw new CidNotFoundResponse();
        }
        if (this.whitelistService.isCIDWhiteListed(request.getCid())) {
            throw new CIDAlreadyWhitelistedResponse();
        }
        if (request.getCid() == null) {
            throw new MissingRequiredFieldResponse("cid");
        }
        this.whitelistService.editWhitelist(oldWhitelist, request.getCid());
        return new EditedWhitelistResponse();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> removeWhitelist(@PathVariable("id") String id) {
        if (!this.whitelistService.isCIDWhiteListed(UUID.fromString(id))) {
            throw new CidNotFoundResponse();
        }
        this.whitelistService.removeWhiteListedCID(UUID.fromString(id));
        return new UserDeletedResponse();
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Whitelist>> getAllWhiteList() {
        return new GetWhitelistedResponse(this.whitelistService.getAllWhitelist());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Whitelist> getWhitelist(@PathVariable("id") String id) {
        if(!this.whitelistService.isCIDWhiteListed(UUID.fromString(id))){
            throw new CidNotFoundResponse();
        }
        return new GetWhitelistResponse(this.whitelistService.getWhitelistById(id));
    }

    /**
     * /whitelist/valid will be able to return whether or not a
     * user is whitelist, without doing anything to modify the data.
     *
     * @param cid CID of a user.
     * @return true if the user is whitelisted false otherwise
     */
    @RequestMapping(value = "/valid/{id}", method = RequestMethod.POST)      // Should this be changed to a Pathvar?
    public boolean isValid(@Valid @RequestBody WhitelistCodeRequest cid, BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        return this.whitelistService.isCIDWhiteListed(cid.getCid());
    }


}
