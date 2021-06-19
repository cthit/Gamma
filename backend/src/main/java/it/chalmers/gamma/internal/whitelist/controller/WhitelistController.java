package it.chalmers.gamma.internal.whitelist.controller;

import it.chalmers.gamma.domain.UserActivation;
import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.internal.useractivation.service.UserActivationService;
import it.chalmers.gamma.internal.whitelist.service.WhitelistService;
import it.chalmers.gamma.mail.MailSenderService;

import javax.validation.Valid;

import it.chalmers.gamma.util.response.SuccessResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/internal/whitelist")
public final class WhitelistController {

    private final UserActivationService userActivationService;
    private final MailSenderService mailSenderService;
    private final WhitelistService whitelistService;

    private static final Logger LOGGER = LoggerFactory.getLogger(WhitelistController.class);

    private static final String MAIL_POSTFIX = "student.chalmers.se";

    public WhitelistController(UserActivationService userActivationService,
                               MailSenderService mailSenderService,
                               WhitelistService whitelistService) {
        this.userActivationService = userActivationService;
        this.mailSenderService = mailSenderService;
        this.whitelistService = whitelistService;
    }

    private record WhitelistCodeRequest(Cid cid) { }

    @PostMapping("/activate_cid")
    public WhitelistedCidActivatedResponse createActivationCode(@Valid @RequestBody WhitelistCodeRequest request) {
        Cid cid = request.cid;

        if (this.whitelistService.cidIsWhitelisted(cid)) {
            UserActivation userActivation = this.userActivationService.saveUserActivation(cid);
            sendEmail(userActivation);
        } else {
            LOGGER.warn(String.format("Non Whitelisted User: %s Tried to Create Account", cid));
        }

        //Gamma doesn't differentiate if activation of a cid was successful or not.
        return new WhitelistedCidActivatedResponse();
    }

    private void sendEmail(UserActivation userActivation) {
        String code = userActivation.token().get();
        String to = userActivation.cid() + "@" + MAIL_POSTFIX;
        String message = "Your code to Gamma is: " + code;
        this.mailSenderService.trySendingMail(to, "Chalmers activationcode code", message);
    }

    // This will be thrown even if there was an error for security reasons.
    private static class WhitelistedCidActivatedResponse extends SuccessResponse { }

}

