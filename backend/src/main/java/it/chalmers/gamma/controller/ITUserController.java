package it.chalmers.gamma.controller;

import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.entity.Whitelist;
import it.chalmers.gamma.exceptions.*;
import it.chalmers.gamma.requests.CreateITUserRequest;
import it.chalmers.gamma.service.ActivationCodeService;
import it.chalmers.gamma.service.ITUserService;
import it.chalmers.gamma.service.WhitelistService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ITUserController {

    private final ITUserService itUserService;
    private final ActivationCodeService activationCodeService;
    private final WhitelistService whitelistService;

    public ITUserController(ITUserService itUserService, ActivationCodeService activationCodeService, WhitelistService whitelistService) {
        this.itUserService = itUserService;
        this.activationCodeService = activationCodeService;
        this.whitelistService = whitelistService;
    }

    @GetMapping
    public List<ITUser> getAllITUsers() {
        return itUserService.loadAllUsers();
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CustomHttpStatus createUser(@RequestBody CreateITUserRequest createITUserRequest){
        if(createITUserRequest == null){
            throw new NullPointerException();
        }
        Whitelist user = whitelistService.getWhitelist(createITUserRequest.getWhitelist().getCid());
        if(user == null){
            return new NoCidFoundException();
        }
        createITUserRequest.setWhitelist(user);
        if(itUserService.userExists(createITUserRequest.getWhitelist().getCid())){
            return new UserAlreadyExistsException();
        }
        if(!activationCodeService.codeMatches(createITUserRequest.getCode(), user.getCid())){
            return new CodeMissmatchException();
        }
        if(activationCodeService.hasCodeExpired(user.getCid(), 2)){
            activationCodeService.deleteCode(user.getCid());
            return new CodeExpiredException();
        }
        else{
            itUserService.createUser(createITUserRequest);
            removeCid(createITUserRequest);
            return new CustomHttpStatus(200, "CREATED_USER", "User Was Created", "");
        }
    }

    private void removeCid(CreateITUserRequest createITUserRequest) {       // Check if this cascades automatically
        activationCodeService.deleteCode(createITUserRequest.getWhitelist().getCid());
        whitelistService.removeWhiteListedCID(createITUserRequest.getWhitelist().getCid());
    }

}
