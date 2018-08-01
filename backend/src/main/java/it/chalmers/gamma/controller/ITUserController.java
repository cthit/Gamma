package it.chalmers.gamma.controller;

import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.entity.Whitelist;
import it.chalmers.gamma.jwt.JwtTokenProvider;
import it.chalmers.gamma.requests.CidPasswordRequest;
import it.chalmers.gamma.requests.CreateITUserRequest;
import it.chalmers.gamma.requests.EditITUserRequest;
import it.chalmers.gamma.response.*;
import it.chalmers.gamma.service.ActivationCodeService;
import it.chalmers.gamma.service.ITUserService;
import it.chalmers.gamma.service.WhitelistService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ITUserController {

    private final ITUserService itUserService;
    private final ActivationCodeService activationCodeService;
    private final WhitelistService whitelistService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public ITUserController(ITUserService itUserService, ActivationCodeService activationCodeService,
                            WhitelistService whitelistService, AuthenticationManager authenticationManager,
                            JwtTokenProvider jwtTokenProvider) {
        this.itUserService = itUserService;
        this.activationCodeService = activationCodeService;
        this.whitelistService = whitelistService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
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
            return new IncorrectCidOrPasswordResponse();
        }
        return new IncorrectCidOrPasswordResponse();
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> createUser(@RequestBody CreateITUserRequest createITUserRequest) {
        if (createITUserRequest == null) {
            throw new NullPointerException();
        }
        Whitelist user = whitelistService.getWhitelist(createITUserRequest.getWhitelist().getCid());
        if (user == null) {
            return new CodeOrCidIsWrongResponse();
        }
        createITUserRequest.setWhitelist(user);
        if (itUserService.userExists(createITUserRequest.getWhitelist().getCid())) {
            return new UserAlreadyExistsResponse();
        }
        if (!activationCodeService.codeMatches(createITUserRequest.getCode(), user.getCid())) {
            return new CodeOrCidIsWrongResponse();
        }
        if (activationCodeService.hasCodeExpired(user.getCid(), 2)) {
            activationCodeService.deleteCode(user.getCid());
            return new CodeExpiredResponse();
        }
        if(createITUserRequest.getPassword().length() < 8){
            return new PasswordTooShortResponse();
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
    public ResponseEntity<ITUser> getMe(@RequestHeader("Authorization") String jwtToken) {
        jwtToken = jwtTokenProvider.removeBearer(jwtToken);
        String cid = jwtTokenProvider.decodeToken(jwtToken).getBody().getSubject();
        ITUser user = itUserService.loadUser(cid);
        return new GetUserResponse(user);
    }
    @RequestMapping(value = "/minified", method = RequestMethod.GET)
    public ResponseEntity<List<ITUser.ITUserView>> getAllUserMini(){
        List<ITUser> itUsers = itUserService.loadAllUsers();
        List<ITUser.ITUserView> minifiedITUsers = new ArrayList<>();
        List<String> props = new ArrayList<>();
        props.add("cid");
        props.add("firstName");
        props.add("lastName");
        props.add("nick");
        props.add("attendanceYear");
        props.add("id");
        for(ITUser user : itUsers){
            minifiedITUsers.add(user.getView(props));
        }
        return new MinifiedUsersResponse(minifiedITUsers);
    }
    @RequestMapping(value = "/{user}", method = RequestMethod.GET)
    public ResponseEntity<ITUser> getUser(@PathVariable("user") String user){
        return new GetUserResponse(itUserService.loadUser(user));
    }

    //TODO I DON'T EVEN FUCKING KNOW AT THIS POINT:::::::::::::::
  /*  @RequestMapping(value = "/{user}", method = RequestMethod.PUT)
    public ResponseEntity<String> editUser(@PathVariable("user") String user){
        ITUser itUser = itUserService.loadUser(user);
        itUserService.editUser();
    }*/
}
