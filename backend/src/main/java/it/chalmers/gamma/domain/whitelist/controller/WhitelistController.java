package it.chalmers.gamma.domain.whitelist.controller;

import it.chalmers.gamma.domain.activationcode.data.ActivationCodeDTO;
import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.whitelist.service.WhitelistFinder;
import it.chalmers.gamma.domain.whitelist.service.WhitelistService;
import it.chalmers.gamma.domain.whitelist.controller.request.WhitelistCodeRequest;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.domain.activationcode.controller.response.ActivationCodeAddedResonse;
import it.chalmers.gamma.domain.activationcode.service.ActivationCodeService;
import it.chalmers.gamma.mail.MailSenderService;

import it.chalmers.gamma.util.InputValidationUtils;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/whitelist", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public final class WhitelistController {

    private final WhitelistService whitelistService;
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
        this.whitelistService = whitelistService;
        this.activationCodeService = activationCodeService;
        this.mailSenderService = mailSenderService;
        this.whitelistFinder = whitelistFinder;
    }

    @PostMapping("/activate_cid")
    public ActivationCodeAddedResonse createActivationCode(@Valid @RequestBody WhitelistCodeRequest request,
                                                       BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }

        Cid cid = new Cid(request.getCid());

        if (this.whitelistFinder.cidIsWhitelisted(cid)) {
            ActivationCodeDTO activationCode = this.activationCodeService.saveActivationCode(cid);
            sendEmail(activationCode);
        } else {
            LOGGER.warn(String.format("Non Whitelisted User: %s Tried to Create Account", cid));
        }

        //Gamma doesn't differentiate if activation of a cid was successful or not.
        return new ActivationCodeAddedResonse();
    }

    private void sendEmail(ActivationCodeDTO activationCode) {
        String code = activationCode.getCode();
        String to = activationCode.getCid() + "@" + MAIL_POSTFIX;
        String message = "Your code to Gamma is: " + code;
        this.mailSenderService.trySendingMail(to, "Chalmers activationcode code", message);
    }
}

