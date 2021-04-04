package it.chalmers.gamma.domain.whitelist.controller;

import it.chalmers.gamma.domain.activationcode.data.dto.ActivationCodeDTO;
import it.chalmers.gamma.util.domain.Cid;
import it.chalmers.gamma.domain.whitelist.service.WhitelistFinder;
import it.chalmers.gamma.domain.whitelist.service.WhitelistService;
import it.chalmers.gamma.domain.whitelist.controller.request.WhitelistCodeRequest;
import it.chalmers.gamma.util.response.InputValidationFailedResponse;
import it.chalmers.gamma.domain.whitelist.controller.response.WhitelistedCidActivatedResponse;
import it.chalmers.gamma.domain.activationcode.service.ActivationCodeService;
import it.chalmers.gamma.mail.MailSenderService;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/whitelist")
public final class WhitelistController {

    private final ActivationCodeService activationCodeService;
    private final MailSenderService mailSenderService;
    private final WhitelistFinder whitelistFinder;

    private static final Logger LOGGER = LoggerFactory.getLogger(WhitelistController.class);

    @Value("${mail.receiver.standard-postfix}")
    private static final String MAIL_POSTFIX = "student.chalmers.se";

    public WhitelistController(
            WhitelistService whitelistService,
            ActivationCodeService activationCodeService,
            MailSenderService mailSenderService,
            WhitelistFinder whitelistFinder) {
        this.activationCodeService = activationCodeService;
        this.mailSenderService = mailSenderService;
        this.whitelistFinder = whitelistFinder;
    }

    @PostMapping("/activate_cid")
    public WhitelistedCidActivatedResponse createActivationCode(@Valid @RequestBody WhitelistCodeRequest request) {
        Cid cid = new Cid(request.getCid());

        if (this.whitelistFinder.cidIsWhitelisted(cid)) {
            ActivationCodeDTO activationCode = this.activationCodeService.saveActivationCode(cid);
            sendEmail(activationCode);
        } else {
            LOGGER.warn(String.format("Non Whitelisted User: %s Tried to Create Account", cid));
        }

        //Gamma doesn't differentiate if activation of a cid was successful or not.
        return new WhitelistedCidActivatedResponse();
    }

    private void sendEmail(ActivationCodeDTO activationCode) {
        String code = activationCode.getCode().get();
        String to = activationCode.getCid() + "@" + MAIL_POSTFIX;
        String message = "Your code to Gamma is: " + code;
        this.mailSenderService.trySendingMail(to, "Chalmers activationcode code", message);
    }
}

