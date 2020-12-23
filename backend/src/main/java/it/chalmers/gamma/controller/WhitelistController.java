package it.chalmers.gamma.controller;

import it.chalmers.gamma.domain.dto.user.ActivationCodeDTO;
import it.chalmers.gamma.domain.dto.user.WhitelistDTO;
import it.chalmers.gamma.user.request.requests.WhitelistCodeRequest;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.response.activationcode.ActivationCodeAddedResonse;
import it.chalmers.gamma.response.whitelist.WhitelistDoesNotExistsException;
import it.chalmers.gamma.service.ActivationCodeService;
import it.chalmers.gamma.service.MailSenderService;
import it.chalmers.gamma.service.WhitelistService;

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
    private static final Logger LOGGER = LoggerFactory.getLogger(WhitelistController.class);
    @Value("${mail.receiver.standard-postfix}")
    private static final String MAIL_POSTFIX = "student.chalmers.se";

    public WhitelistController(
            WhitelistService whitelistService,
            ActivationCodeService activationCodeService,
            MailSenderService mailSenderService) {
        this.whitelistService = whitelistService;
        this.activationCodeService = activationCodeService;
        this.mailSenderService = mailSenderService;
    }

    @PostMapping("/activate_cid")
    public ActivationCodeAddedResonse createActivationCode(@Valid @RequestBody WhitelistCodeRequest cid,
                                                       BindingResult result) {
        try {
            if (result.hasErrors()) {
                throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
            }
            if (this.whitelistService.isCIDWhiteListed(cid.getCid())) {
                WhitelistDTO whitelist = this.whitelistService.getWhitelist(cid.getCid());
                ActivationCodeDTO activationCode = this.activationCodeService.saveActivationCode(whitelist);
                sendEmail(activationCode);
            } else {
                String nonWhitelistWarning = "Non Whitelisted User: %s Tried to Create Account";
                LOGGER.warn(String.format(nonWhitelistWarning, cid.getCid()));
            }
            return new ActivationCodeAddedResonse(); // For security reasons
        } catch (WhitelistDoesNotExistsException e) {   // This should never happen.
            return new ActivationCodeAddedResonse();
        }
    }

    private void sendEmail(ActivationCodeDTO activationCode) {
        String code = activationCode.getCode();
        String to = activationCode.getWhitelistDTO().getCid() + "@" + MAIL_POSTFIX;
        String message = "Your code to Gamma is: " + code;
        this.mailSenderService.trySendingMail(to, "Chalmers activation code", message);
    }
}

