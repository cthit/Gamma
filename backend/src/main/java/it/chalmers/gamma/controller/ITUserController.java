package it.chalmers.gamma.controller;

import static it.chalmers.gamma.db.serializers.ITUserSerializer.Properties.ACCEPTANCE_YEAR;
import static it.chalmers.gamma.db.serializers.ITUserSerializer.Properties.CID;
import static it.chalmers.gamma.db.serializers.ITUserSerializer.Properties.FIRST_NAME;
import static it.chalmers.gamma.db.serializers.ITUserSerializer.Properties.ID;
import static it.chalmers.gamma.db.serializers.ITUserSerializer.Properties.LAST_NAME;
import static it.chalmers.gamma.db.serializers.ITUserSerializer.Properties.NICK;

import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.entity.Whitelist;
import it.chalmers.gamma.db.serializers.ITUserSerializer;
import it.chalmers.gamma.jwt.JwtTokenProvider;
import it.chalmers.gamma.requests.CidPasswordRequest;
import it.chalmers.gamma.requests.CreateITUserRequest;
import it.chalmers.gamma.response.CidNotFoundResponse;
import it.chalmers.gamma.response.CodeExpiredResponse;
import it.chalmers.gamma.response.CodeOrCidIsWrongResponse;
import it.chalmers.gamma.response.IncorrectCidOrPasswordResponse;
import it.chalmers.gamma.response.LoginCompleteResponse;
import it.chalmers.gamma.response.NoDataSentResponse;
import it.chalmers.gamma.response.PasswordTooShortResponse;
import it.chalmers.gamma.response.UserAlreadyExistsResponse;
import it.chalmers.gamma.response.UserCreatedResponse;
import it.chalmers.gamma.service.ActivationCodeService;
import it.chalmers.gamma.service.ITUserService;
import it.chalmers.gamma.service.UserWebsiteService;
import it.chalmers.gamma.service.WebsiteView;
import it.chalmers.gamma.service.WhitelistService;

import java.security.Principal;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("PMD.ExcessiveImports")
@RestController
@RequestMapping(value = "/app/users", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public final class ITUserController {

    private final ITUserService itUserService;
    private final ActivationCodeService activationCodeService;
    private final WhitelistService whitelistService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserWebsiteService userWebsiteService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ITUserController.class);

    public ITUserController(ITUserService itUserService,
                             ActivationCodeService activationCodeService,
                             WhitelistService whitelistService,
                             AuthenticationManager authenticationManager,
                             JwtTokenProvider jwtTokenProvider,
                             UserWebsiteService userWebsiteService) {
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
            Authentication authentication =
                    this.authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    cidPasswordRequest.getCid(),
                                    cidPasswordRequest.getPassword()
                            )
                    );
            if (authentication.isAuthenticated()) {
                String jwt = this.jwtTokenProvider.createToken(cidPasswordRequest.getCid());
                return new LoginCompleteResponse(jwt);
            }
        } catch (AuthenticationException e) {
            LOGGER.info(e.getMessage(), e);
            throw new IncorrectCidOrPasswordResponse();
        }
        throw new IncorrectCidOrPasswordResponse();
    }


    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    @SuppressWarnings("PMD.CyclomaticComplexity")
    public ResponseEntity<String> createUser(
            @RequestBody CreateITUserRequest createITUserRequest) {
        if (createITUserRequest == null) {
            throw new NoDataSentResponse();
        }

        Whitelist user = this.whitelistService.getWhitelist(
                createITUserRequest.getWhitelist().getCid()
        );

        if (user == null) {
            throw new CodeOrCidIsWrongResponse();
        }

        createITUserRequest.setWhitelist(user);

        if (this.itUserService.userExists(createITUserRequest.getWhitelist().getCid())) {
            throw new UserAlreadyExistsResponse();
        }

        if (!this.activationCodeService.codeMatches(createITUserRequest.getCode(), user.getCid())) {
            throw new CodeOrCidIsWrongResponse();
        }

        if (this.activationCodeService.hasCodeExpired(user.getCid(), 2)) {
            this.activationCodeService.deleteCode(user.getCid());
            throw new CodeExpiredResponse();
        }

        int minPassLength = 8;

        if (createITUserRequest.getPassword().length() < minPassLength) {
            throw new PasswordTooShortResponse();
        } else {
            this.itUserService.createUser(
                    createITUserRequest.getNick(),
                    createITUserRequest.getFirstName(),
                    createITUserRequest.getLastName(),
                    createITUserRequest.getWhitelist().getCid(),
                    Year.of(createITUserRequest.getAcceptanceYear()),
                    createITUserRequest.isUserAgreement(),
                    null,
                    createITUserRequest.getPassword());
            removeCidFromWhitelist(createITUserRequest);
            return new UserCreatedResponse();
        }
    }


    // Check if this cascades automatically
    private void removeCidFromWhitelist(CreateITUserRequest createITUserRequest) {
        this.activationCodeService.deleteCode(createITUserRequest.getWhitelist().getCid());
        this.whitelistService.removeWhiteListedCID(createITUserRequest.getWhitelist().getCid());
    }

    @RequestMapping(value = "/me", method = RequestMethod.GET)
    public JSONObject getMe(@RequestHeader("Authorization") String jwtTokenWithBearer, Principal principal) {
        System.out.println("principal " + principal);
        System.out.println("ds" + principal.getName());
        String cid = principal.getName();
        ITUser user = this.itUserService.loadUser(cid);
        ITUserSerializer serializer =
                new ITUserSerializer(ITUserSerializer.Properties.getAllProperties());
        List<WebsiteView> websites =
                this.userWebsiteService.getWebsitesOrdered(
                        this.userWebsiteService.getWebsites(user)
                );
        return serializer.serialize(user, websites);
    }

    @RequestMapping(value = "/minified", method = RequestMethod.GET)
    public List<JSONObject> getAllUserMini() {
        List<ITUser> itUsers = this.itUserService.loadAllUsers();
        List<ITUserSerializer.Properties> props =
                new ArrayList<>(Arrays.asList(
                        CID,
                        FIRST_NAME,
                        LAST_NAME,
                        NICK,
                        ACCEPTANCE_YEAR,
                        ID
                ));
        List<JSONObject> minifiedITUsers = new ArrayList<>();
        ITUserSerializer serializer = new ITUserSerializer(props);
        for (ITUser user : itUsers) {
            minifiedITUsers.add(serializer.serialize(user, null));
        }
        return minifiedITUsers;
    }

    @RequestMapping(value = "/{cid}", method = RequestMethod.GET)
    public JSONObject getUser(@PathVariable("cid") String cid) {
        ITUser user = this.itUserService.loadUser(cid);
        if (user == null) {
            throw new CidNotFoundResponse();
        }
        ITUserSerializer serializer = new ITUserSerializer(
                ITUserSerializer.Properties.getAllProperties()
        );
        List<WebsiteView> websites =
                this.userWebsiteService.getWebsitesOrdered(
                        this.userWebsiteService.getWebsites(user)
                );
        return serializer.serialize(user, websites);
    }
}
