package it.chalmers.delta.controller;

import it.chalmers.delta.db.entity.ActivationCode;
import it.chalmers.delta.db.entity.Whitelist;
import it.chalmers.delta.requests.WhitelistCodeRequest;
import it.chalmers.delta.response.ActivationCodeAddedResonse;
import it.chalmers.delta.response.InputValidationFailedResponse;
import it.chalmers.delta.service.ActivationCodeService;
import it.chalmers.delta.service.MailSenderService;
import it.chalmers.delta.service.WhitelistService;

import it.chalmers.delta.util.InputValidationUtils;
import it.chalmers.delta.util.TokenUtils;

import javax.validation.Valid;

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
            String code = TokenUtils.generateToken(15, TokenUtils.CharacterTypes.NUMBERS);
            ActivationCode activationCode = this.activationCodeService.saveActivationCode(whitelist, code);
            sendEmail(activationCode);
        }
        return new ActivationCodeAddedResonse(); // For security reasons
    }

    private void sendEmail(ActivationCode activationCode) {
        String code = activationCode.getCode();
        String to = activationCode.getCid() + "@" + MAIL_POSTFIX;
        String message = "Your code to Delta is: " + code;
        this.mailSenderService.trySendingMail(to, "Chalmers activation code", message);
    }
}

