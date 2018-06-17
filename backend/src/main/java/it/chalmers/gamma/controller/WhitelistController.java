package it.chalmers.gamma.controller;

import it.chalmers.gamma.db.entity.Whitelist;
import it.chalmers.gamma.service.WhitelistService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.List;

@RestController
@RequestMapping(value = "/whitelist", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class WhitelistController {

    WhitelistService whitelistService;

    public WhitelistController(WhitelistService whitelistService){
        this.whitelistService = whitelistService;
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
    public void createActivationCode(@RequestBody Whitelist cid){
        if(whitelistService.isCIDWhiteListed(cid.getCid())){

        }
    }
    private String generateCode(){
        return "";
    }
}

