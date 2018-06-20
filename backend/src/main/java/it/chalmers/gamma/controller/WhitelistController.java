package it.chalmers.gamma.controller;

import it.chalmers.gamma.db.entity.Whitelist;
import it.chalmers.gamma.exceptions.NoCidFoundException;
import it.chalmers.gamma.exceptions.UserAlreadyExistsException;
import it.chalmers.gamma.service.ActivationCodeService;
import it.chalmers.gamma.service.ITUserService;
import it.chalmers.gamma.service.WhitelistService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/whitelist", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class WhitelistController {

    WhitelistService whitelistService;

    ActivationCodeService activationCodeService;

    ITUserService itUserService;

    public WhitelistController(WhitelistService whitelistService, ActivationCodeService activationCodeService, ITUserService itUserService){
        this.whitelistService = whitelistService;
        this.activationCodeService = activationCodeService;
        this.itUserService = itUserService;

    }
    @GetMapping
    public List<Whitelist> getAllWhiteListed(){
        return whitelistService.findAll();
    }

    /**
     * /whitelist/valid will be able to return whether or not a user is whitelist, without doing anything to modify the data.
     * @param cid CID of a user.
     * @return true if the user is whitelisted false otherwise
     */
    @RequestMapping(value = "/valid", method = RequestMethod.POST)
    public boolean isValid(@RequestBody Whitelist cid) {
        System.out.println(cid);
        if (whitelistService.isCIDWhiteListed(cid.getCid())) {
            return true;
        }
        return false;
    }
    //TODO should probably return something to tell the backend whether or not creating the account was successful.
    @PostMapping
    public boolean createActivationCode(@RequestBody Whitelist cid){
        if(itUserService.userExists(cid.getCid())){
            try {
                throw new UserAlreadyExistsException();
            } catch (UserAlreadyExistsException e) {
                e.printStackTrace();
                return false;
            }
        }
        if(whitelistService.isCIDWhiteListed(cid.getCid())) {
            Whitelist whitelist = whitelistService.findByCid(cid.getCid());
            activationCodeService.generateAndSaveActivationCode(whitelist);
            return true;
        }
        else{
            try {
                throw new NoCidFoundException();
            } catch (NoCidFoundException e) {
                e.printStackTrace();
                return false;
            }
        }
    }
}

