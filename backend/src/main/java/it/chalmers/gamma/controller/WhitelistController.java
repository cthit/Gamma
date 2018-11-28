package it.chalmers.gamma.controller;

import it.chalmers.gamma.db.entity.ActivationCode;
import it.chalmers.gamma.db.entity.Whitelist;
import it.chalmers.gamma.requests.WhitelistCodeRequest;
import it.chalmers.gamma.response.WhitelistAddedResponse;
import it.chalmers.gamma.service.ActivationCodeService;
import it.chalmers.gamma.service.MailSenderService;
import it.chalmers.gamma.service.WhitelistService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/whitelist", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class WhitelistController {

    private WhitelistService whitelistService;

    private ActivationCodeService activationCodeService;

    private MailSenderService mailSenderService;

    // @Value("${mail.receiver.standard-postfix}")
    private String mailPostfix = "@student.chalmers.se";

    public WhitelistController(
            WhitelistService whitelistService,
            ActivationCodeService activationCodeService,
            MailSenderService mailSenderService) {
        this.whitelistService = whitelistService;
        this.activationCodeService = activationCodeService;
        this.mailSenderService = mailSenderService;

    }

    @RequestMapping(value = "/activate_cid", method = RequestMethod.POST)
    public ResponseEntity<String> createActivationCode(@RequestBody WhitelistCodeRequest cid) {
        if (this.whitelistService.isCIDWhiteListed(cid.getCid())) {
            Whitelist whitelist = this.whitelistService.getWhitelist(cid.getCid());
            String code = this.activationCodeService.generateActivationCode();
            ActivationCode activationCode = this.activationCodeService.saveActivationCode(whitelist, code);
            System.out.println("Send mail with code" + activationCode);
            // sendEmail(activationCode);
        }

        /*
        We always want to send the same response, for security reasons.
        If the responses would vary, brute force attacks could be made
        to find out real CID values.
         */
        return new WhitelistAddedResponse();
    }
    private void sendEmail(ActivationCode activationCode) {
        String code = activationCode.getCode();
        String to = activationCode.getCid() + "@" + this.mailPostfix;
        String message = "Your code to Gamma is: " + code;
        this.mailSenderService.sendMail(to, "Chalmers activation code", message);
    }
}

