package it.chalmers.gamma.controller;

import it.chalmers.gamma.domain.dto.user.ActivationCodeDTO;
import it.chalmers.gamma.domain.dto.user.WhitelistDTO;
import it.chalmers.gamma.requests.WhitelistCodeRequest;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.response.activationcode.ActivationCodeAddedResonse;
import it.chalmers.gamma.service.ActivationCodeService;
import it.chalmers.gamma.service.MailSenderService;
import it.chalmers.gamma.service.WhitelistService;

import it.chalmers.gamma.util.InputValidationUtils;
import it.chalmers.gamma.util.TokenUtils;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
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

    @Value("${mail.receiver.standard-postfix}")
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
    public ActivationCodeAddedResonse createActivationCode(@Valid @RequestBody WhitelistCodeRequest cid,
                                                       BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        if (this.whitelistService.isCIDWhiteListed(cid.getCid())) {
            WhitelistDTO whitelist = this.whitelistService.getWhitelistDTO(cid.getCid());
            String code = TokenUtils.generateToken(15, TokenUtils.CharacterTypes.NUMBERS);
            ActivationCodeDTO activationCode = this.activationCodeService.saveActivationCode(whitelist, code);
            sendEmail(activationCode);
        }
        return new ActivationCodeAddedResonse(); // For security reasons
    }

    private void sendEmail(ActivationCodeDTO activationCode) {
        String code = activationCode.getCode();
        String to = activationCode.getWhitelistDTO().getCid() + "@" + MAIL_POSTFIX;
        String message = "Your code to Gamma is: " + code;
        this.mailSenderService.trySendingMail(to, "Chalmers activation code", message);
    }
}

