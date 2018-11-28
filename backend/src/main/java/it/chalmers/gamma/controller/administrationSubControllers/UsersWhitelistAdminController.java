package it.chalmers.gamma.controller.administrationSubControllers;

import it.chalmers.gamma.db.entity.Whitelist;
import it.chalmers.gamma.requests.AddListOfWhitelistedRequest;
import it.chalmers.gamma.requests.WhitelistCodeRequest;
import it.chalmers.gamma.response.*;
import it.chalmers.gamma.service.ITUserService;
import it.chalmers.gamma.service.WhitelistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/users/whitelist")
public class UsersWhitelistAdminController {

    private WhitelistService whitelistService;
    private ITUserService itUserService;

    public UsersWhitelistAdminController(WhitelistService whitelistService, ITUserService itUserService){
        this.whitelistService = whitelistService;
        this.itUserService = itUserService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> addWhitelistedUsers(@RequestBody AddListOfWhitelistedRequest request) {
        List<String> cids = request.getCids();
        for(String cid : cids) {
            if (whitelistService.isCIDWhiteListed(cid)) {
                throw new CIDAlreadyWhitelistedResponse();
            }
            if (itUserService.userExists(cid)) {
                throw new UserAlreadyExistsResponse();
            }
            whitelistService.addWhiteListedCID(cid);
        }
        return new WhitelistAddedResponse();
    }
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<String> editWhitelist(@RequestBody WhitelistCodeRequest request, @PathVariable("id") String id){
        Whitelist oldWhitelist = whitelistService.getWhitelistById(id);
        if(!whitelistService.isCIDWhiteListed(oldWhitelist.getCid())){
            throw new CidNotFoundResponse();
        }
        if(whitelistService.isCIDWhiteListed(request.getCid())){
            throw new CIDAlreadyWhitelistedResponse();
        }
        if(request.getCid() == null){
            throw new MissingRequiredFieldResponse("cid");
        }
        whitelistService.editWhitelist(oldWhitelist, request.getCid());
        return new EditedWhitelistResponse();
    }
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> removeWhitelist(@PathVariable("id") String id){
        Whitelist whitelist = whitelistService.getWhitelistById(id);
        if(whitelist == null){
            throw new CidNotFoundResponse();
        }
        whitelistService.removeWhiteListedCID(whitelist.getCid());
        return new UserDeletedResponse();
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Whitelist>> getAllWhiteList(){
        return new GetWhitelistedResponse(whitelistService.getAllWhitelist());
    }

    /**
     * /whitelist/valid will be able to return whether or not a user is whitelist, without doing anything to modify the data.
     * @param cid CID of a user.
     * @return true if the user is whitelisted false otherwise
     */
    @RequestMapping(value = "/valid", method = RequestMethod.POST)
    public boolean isValid(@RequestBody WhitelistCodeRequest cid) {
        return whitelistService.isCIDWhiteListed(cid.getCid());
    }


}
