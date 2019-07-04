package it.chalmers.gamma.controller;

import it.chalmers.gamma.db.entity.ActivationCode;
import it.chalmers.gamma.db.entity.Whitelist;
import it.chalmers.gamma.requests.WhitelistCodeRequest;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.response.WhitelistAddedResponse;
import it.chalmers.gamma.service.ActivationCodeService;
import it.chalmers.gamma.service.MailSenderService;
import it.chalmers.gamma.service.WhitelistService;

import it.chalmers.gamma.util.InputValidationUtils;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/whitelist", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public final class WhitelistController {

    private final WhitelistService whitelistService;
    private final ActivationCodeService activationCodeService;
    private final MailSenderService mailSenderService;

    // @Value("${mail.receiver.standard-postfix}")
    private static final String MAIL_POSTFIX = "@student.chalmers.se";
    private static final Logger LOGGER = LoggerFactory.getLogger(WhitelistController.class);

    private String profile;

    public WhitelistController(
            WhitelistService whitelistService,
            ActivationCodeService activationCodeService,
            MailSenderService mailSenderService) {
        this.whitelistService = whitelistService;
        this.activationCodeService = activationCodeService;
        this.mailSenderService = mailSenderService;

    }

    @RequestMapping(value = "/activate_cid", method = RequestMethod.POST)
    public ResponseEntity<String> createActivationCode(@Valid @RequestBody WhitelistCodeRequest cid,
                                                       BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        if (this.whitelistService.isCIDWhiteListed(cid.getCid())) {
            Whitelist whitelist = this.whitelistService.getWhitelist(cid.getCid());
            String code = this.activationCodeService.generateActivationCode();
            ActivationCode activationCode = this.activationCodeService.saveActivationCode(whitelist, code);
            sendEmail(activationCode);
        }
        return new WhitelistAddedResponse(); // For security reasons
    }

    private void sendEmail(ActivationCode activationCode) {
        String code = activationCode.getCode();
        String to = activationCode.getCid() + "@" + MAIL_POSTFIX;
        String message = "Your code to Gamma is: " + code;
        this.mailSenderService.trySendingMail(to, "Chalmers activation code", message);
    }
}

