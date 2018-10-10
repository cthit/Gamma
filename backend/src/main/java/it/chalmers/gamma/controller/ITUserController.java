package it.chalmers.gamma.controller;

import it.chalmers.gamma.db.entity.*;
import it.chalmers.gamma.db.serializers.FKITGroupSerializer;
import it.chalmers.gamma.db.serializers.ITUserSerializer;
import it.chalmers.gamma.jwt.JwtTokenProvider;
import it.chalmers.gamma.requests.CidPasswordRequest;
import it.chalmers.gamma.requests.CreateITUserRequest;
import it.chalmers.gamma.response.*;
import it.chalmers.gamma.service.*;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static it.chalmers.gamma.db.serializers.ITUserSerializer.Properties.*;

@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ITUserController {

    private final ITUserService itUserService;
    private final ActivationCodeService activationCodeService;
    private final WhitelistService whitelistService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserWebsiteService userWebsiteService;

    public ITUserController(ITUserService itUserService, ActivationCodeService activationCodeService,
                            WhitelistService whitelistService, AuthenticationManager authenticationManager,
                            JwtTokenProvider jwtTokenProvider, UserWebsiteService userWebsiteService) {
        this.itUserService = itUserService;
        this.activationCodeService = activationCodeService;
        this.whitelistService = whitelistService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userWebsiteService = userWebsiteService;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<String> login(@RequestBody CidPasswordRequest cidPasswordRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(cidPasswordRequest.getCid(), cidPasswordRequest.getPassword()));
            if (authentication.isAuthenticated()) {
                String jwt = jwtTokenProvider.createToken(cidPasswordRequest.getCid());
                return new LoginCompleteResponse(jwt);
            }
        } catch (AuthenticationException e) {
            throw new IncorrectCidOrPasswordResponse();
        }
        throw new IncorrectCidOrPasswordResponse();
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> createUser(@RequestBody CreateITUserRequest createITUserRequest) {
        if (createITUserRequest == null) {
            throw new NullPointerException();
        }
        Whitelist user = whitelistService.getWhitelist(createITUserRequest.getWhitelist().getCid());
        if (user == null) {
            throw new CodeOrCidIsWrongResponse();
        }
        createITUserRequest.setWhitelist(user);
        if (itUserService.userExists(createITUserRequest.getWhitelist().getCid())) {
            throw new UserAlreadyExistsResponse();
        }
        if (!activationCodeService.codeMatches(createITUserRequest.getCode(), user.getCid())) {
            throw new CodeOrCidIsWrongResponse();
        }
        if (activationCodeService.hasCodeExpired(user.getCid(), 2)) {
            activationCodeService.deleteCode(user.getCid());
            throw new CodeExpiredResponse();
        }
        if(createITUserRequest.getPassword().length() < 8){
            throw new PasswordTooShortResponse();
        }
            else {
            itUserService.createUser(createITUserRequest.getNick(), createITUserRequest.getFirstName(),
                    createITUserRequest.getLastName(), createITUserRequest.getWhitelist().getCid(),
                    Year.of(createITUserRequest.getAcceptanceYear()), createITUserRequest.isUserAgreement(), null, createITUserRequest.getPassword());
            removeCidFromWhitelist(createITUserRequest);
            return new UserCreatedResponse();
        }
    }


    private void removeCidFromWhitelist(CreateITUserRequest createITUserRequest) {       // Check if this cascades automatically
        activationCodeService.deleteCode(createITUserRequest.getWhitelist().getCid());
        whitelistService.removeWhiteListedCID(createITUserRequest.getWhitelist().getCid());
    }

    @RequestMapping(value = "/me", method = RequestMethod.GET)
    public JSONObject getMe(@RequestHeader("Authorization") String jwtToken) {
        jwtToken = jwtTokenProvider.removeBearer(jwtToken);
        String cid = jwtTokenProvider.decodeToken(jwtToken).getBody().getSubject();
        ITUser user = itUserService.loadUser(cid);
        ITUserSerializer serializer = new ITUserSerializer(ITUserSerializer.Properties.getAllProperties());
        List<EntityWebsiteService.WebsiteView> websites = userWebsiteService.getWebsitesOrdered(userWebsiteService.getWebsites(user));
        return serializer.serialize(user, websites);
    }
    @RequestMapping(value = "/minified", method = RequestMethod.GET)
    public List<JSONObject> getAllUserMini(){
        List<ITUser> itUsers = itUserService.loadAllUsers();
        List<ITUserSerializer.Properties> props = new ArrayList<>(Arrays.asList(CID, FIRST_NAME, LAST_NAME, NICK, ACCEPTANCE_YEAR, ID));
        List<JSONObject> minifiedITUsers = new ArrayList<>();
        ITUserSerializer serializer = new ITUserSerializer(props);
        for(ITUser user : itUsers){
            minifiedITUsers.add(serializer.serialize(user, null));
        }
        return minifiedITUsers;
    }
    @RequestMapping(value = "/{cid}", method = RequestMethod.GET)
    public JSONObject getUser(@PathVariable("cid") String cid){
        List<ITUserSerializer.Properties> properties = ITUserSerializer.Properties.getAllProperties();
        ITUser user = itUserService.loadUser(cid);
        if(user == null){
            throw new CidNotFoundResponse();
        }
        ITUserSerializer serializer = new ITUserSerializer(properties);
        List<EntityWebsiteService.WebsiteView> websites = userWebsiteService.getWebsitesOrdered(userWebsiteService.getWebsites(user));
        return serializer.serialize(user, websites);
    }
}
