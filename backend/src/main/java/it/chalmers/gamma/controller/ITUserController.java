package it.chalmers.gamma.controller;

import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.requests.CreateITUserRequest;
import it.chalmers.gamma.service.ITUserService;
import it.chalmers.gamma.service.WhitelistService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ITUserController {

    private final ITUserService itUserService;
    private final WhitelistService whitelistService;


    public ITUserController(ITUserService itUserService, WhitelistService whitelistService) {
        this.itUserService = itUserService;
        this.whitelistService = whitelistService;
    }

    @GetMapping
    public List<ITUser> getAllITUsers() {
        return itUserService.findAll();
    }

    /**
     * {
     *     code: String,
     *     user: ITUser
     * }
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String createUser(@RequestBody CreateITUserRequest createITUserRequest){
        itUserService.createUser(createITUserRequest);
        return "Yes!";
    }

}
