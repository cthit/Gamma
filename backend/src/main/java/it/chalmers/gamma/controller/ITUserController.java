package it.chalmers.gamma.controller;

import it.chalmers.gamma.db.entity.ActivationCode;
import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.entity.Whitelist;
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
        return itUserService.findAll();
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String createUser(@RequestBody CreateITUserRequest createITUserRequest){
        createITUserRequest.setCid(whitelistService.findByCid(createITUserRequest.getCid().getCid()));
        System.out.println(createITUserRequest);
        if(activationCodeService.codeMatches(createITUserRequest.getCode(), createITUserRequest.getCid())){
            itUserService.createUser(createITUserRequest);
        }
        //validate createITUserRequest.getCode()
        return "Yes!";
    }

}
