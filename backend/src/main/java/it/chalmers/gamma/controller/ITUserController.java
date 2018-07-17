package it.chalmers.gamma.controller;

import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.entity.Whitelist;
import it.chalmers.gamma.jwt.JwtTokenProvider;
import it.chalmers.gamma.requests.CidPasswordRequest;
import it.chalmers.gamma.response.*;
import it.chalmers.gamma.requests.CreateITUserRequest;
import it.chalmers.gamma.service.ActivationCodeService;
import it.chalmers.gamma.service.ITUserService;
import it.chalmers.gamma.service.WhitelistService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
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
    public ResponseEntity<String> login(@RequestBody CidPasswordRequest cidPasswordRequest, HttpServletResponse response) {
        System.out.println("cid: " + cidPasswordRequest.getCid());
        System.out.println("password: " + cidPasswordRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(cidPasswordRequest.getCid(), cidPasswordRequest.getPassword()));
            if (authentication.isAuthenticated()) {
                try {
                    String jwt = jwtTokenProvider.createToken(cidPasswordRequest.getCid());
                    return new SigninCompleteResponse(jwt);
                }
                catch (Exception e) {
                    return new IncorrectCidOrPasswordResponse();
                }
            }
            return new IncorrectCidOrPasswordResponse();
    }

    @GetMapping
    public List<ITUser> getAllITUsers() {
        return itUserService.loadAllUsers();
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
        } else {
            itUserService.createUser(createITUserRequest);
            removeCid(createITUserRequest);
            return new UserCreatedResponse();
        }
    }

    private void removeCid(CreateITUserRequest createITUserRequest) {       // Check if this cascades automatically
        activationCodeService.deleteCode(createITUserRequest.getWhitelist().getCid());
        whitelistService.removeWhiteListedCID(createITUserRequest.getWhitelist().getCid());
    }

}
