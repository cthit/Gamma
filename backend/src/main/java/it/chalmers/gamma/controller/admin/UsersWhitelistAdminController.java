package it.chalmers.gamma.controller.admin;

import it.chalmers.gamma.domain.dto.user.WhitelistDTO;
import it.chalmers.gamma.requests.AddListOfWhitelistedRequest;
import it.chalmers.gamma.requests.WhitelistCodeRequest;

import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.response.user.UserAlreadyExistsResponse;
import it.chalmers.gamma.response.user.UserDeletedResponse;
import it.chalmers.gamma.response.user.UserNotFoundResponse;
import it.chalmers.gamma.response.whitelist.EditedWhitelistResponse;
import it.chalmers.gamma.response.whitelist.GetAllWhitelistResponse;
import it.chalmers.gamma.response.whitelist.GetAllWhitelistResponse.GetAllWhitelistResponseObject;
import it.chalmers.gamma.response.whitelist.GetWhitelistResponse;
import it.chalmers.gamma.response.whitelist.GetWhitelistResponse.GetWhitelistResponseObject;

import it.chalmers.gamma.response.whitelist.WhitelistAddedResponse;
import it.chalmers.gamma.response.whitelist.WhitelistAlreadyAddedException;
import it.chalmers.gamma.response.whitelist.WhitelistIsValidResponse;
import it.chalmers.gamma.response.whitelist.WhitelistIsValidResponse.WhitelistIsValidResponseObject;
import it.chalmers.gamma.service.ITUserService;
import it.chalmers.gamma.service.WhitelistService;
import it.chalmers.gamma.util.InputValidationUtils;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.CyclomaticComplexity", "PMD.ExcessiveImports"})
@RestController
@RequestMapping("/admin/users/whitelist")
public final class UsersWhitelistAdminController {

    private final WhitelistService whitelistService;
    private final ITUserService itUserService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UsersWhitelistAdminController.class);

    public UsersWhitelistAdminController(WhitelistService whitelistService,
                                         ITUserService itUserService) {
        this.whitelistService = whitelistService;
        this.itUserService = itUserService;
    }

    @PostMapping()
    public WhitelistAddedResponse addWhitelistedUsers(
            @Valid @RequestBody AddListOfWhitelistedRequest request, BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        List<String> cids = request.getCids();
        int numNotAdded = 0;

        for (String cid : cids) {           // TODO move this to service
            try {
                if (this.whitelistService.isCIDWhiteListed(cid)) {
                    throw new WhitelistAlreadyAddedException();
                }
                if (this.itUserService.userExists(cid)) {
                    throw new UserAlreadyExistsResponse();
                }
                this.whitelistService.addWhiteListedCID(cid);
                LOGGER.info("Added user " + cid + " to whitelist");
            } catch (UserAlreadyExistsResponse | WhitelistAlreadyAddedException e) {
                LOGGER.info("Did not add user " + cid + " message: " + e.getMessage());
                numNotAdded++;
            }
        }
        int numAdded = cids.size() - numNotAdded;
        if (numAdded == 0) {
            throw new UserAlreadyExistsResponse();
        }
        return new WhitelistAddedResponse(numAdded, numNotAdded);
    }

    /**
     * /whitelist/valid will be able to return whether or not a
     * user is whitelist, without doing anything to modify the data.
     *
     * @return true if the user is whitelisted false otherwise
     */
    @GetMapping("/{id}/valid")      // Should this be changed to a Pathvar?
    public WhitelistIsValidResponseObject validCid(@PathVariable("id") String id) {

        return new WhitelistIsValidResponse(this.whitelistService.isCIDWhiteListed(id)).toResponseObject();
    }

    @PutMapping("/{id}")
    public EditedWhitelistResponse editWhitelist(
            @Valid @RequestBody WhitelistCodeRequest request,
            @PathVariable("id") String id,
            BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        WhitelistDTO oldWhitelist = this.whitelistService.getWhitelist(id);
        if (!this.whitelistService.isCIDWhiteListed(oldWhitelist.getCid())) {
            throw new UserNotFoundResponse();
        }
        if (this.whitelistService.isCIDWhiteListed(request.getCid())) {
            throw new WhitelistAlreadyAddedException();
        }
        this.whitelistService.editWhitelist(oldWhitelist, request.getCid());
        return new EditedWhitelistResponse();
    }

    @DeleteMapping("/{id}")
    public UserDeletedResponse removeWhitelist(@PathVariable("id") String id) {
        if (!this.whitelistService.isCIDWhiteListed(id)) {
            throw new UserNotFoundResponse();
        }
        this.whitelistService.removeWhiteListedCID(id);
        return new UserDeletedResponse();
    }

    @GetMapping()
    public GetAllWhitelistResponseObject getAllWhiteList() {
        List<GetWhitelistResponse> whitelistResponses = this.whitelistService.getAllWhitelist()
                .stream().map(GetWhitelistResponse::new).collect(Collectors.toList());
        return new GetAllWhitelistResponse(whitelistResponses).toResponseObject();
    }

    @GetMapping("/{id}")
    public GetWhitelistResponseObject getWhitelist(@PathVariable("id") String id) {
        return new GetWhitelistResponse(this.whitelistService.getWhitelist(id)).toResponseObject();
    }



}
