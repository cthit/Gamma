package it.chalmers.gamma.controller;

import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.entity.Whitelist;
import it.chalmers.gamma.exceptions.CodeMissmatchException;
import it.chalmers.gamma.exceptions.NoCidFoundException;
import it.chalmers.gamma.exceptions.PasswordTooShortException;
import it.chalmers.gamma.exceptions.UserAlreadyExistsException;
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
    public String createUser(@RequestBody CreateITUserRequest createITUserRequest) throws NoCidFoundException, UserAlreadyExistsException, CodeMissmatchException, PasswordTooShortException {
        if(createITUserRequest == null){
            throw new NullPointerException();
        }
        Whitelist user = whitelistService.getWhitelist(createITUserRequest.getWhitelist().getCid());
        if(user == null){
            throw new NoCidFoundException();
        }
        createITUserRequest.setWhitelist(user);
        if(itUserService.userExists(createITUserRequest.getWhitelist().getCid())){
            throw new UserAlreadyExistsException();
        }
        if(!activationCodeService.codeMatches(createITUserRequest.getCode(), user)){
            throw new CodeMissmatchException();
        }
        else{
            itUserService.createUser(createITUserRequest);
            return "User Was Created";
        }
    }

}
