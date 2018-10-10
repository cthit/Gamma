package it.chalmers.gamma.controller;

import it.chalmers.gamma.db.entity.ActivationCode;
import it.chalmers.gamma.db.entity.Whitelist;
import it.chalmers.gamma.requests.WhitelistCodeRequest;
import it.chalmers.gamma.response.WhitelistAddedResponse;
import it.chalmers.gamma.service.ActivationCodeService;
import it.chalmers.gamma.service.ITUserService;
import it.chalmers.gamma.service.MailSenderService;
import it.chalmers.gamma.service.WhitelistService;
import org.springframework.beans.factory.annotation.Value;
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

    private ITUserService itUserService;

    private MailSenderService mailSenderService;

    @Value("${mail.receiver.standard-postfix}")
    private String mailPostfix;

    public WhitelistController(WhitelistService whitelistService, ActivationCodeService activationCodeService,
                               ITUserService itUserService, MailSenderService mailSenderService){
        this.whitelistService = whitelistService;
        this.activationCodeService = activationCodeService;
        this.itUserService = itUserService;
        this.mailSenderService = mailSenderService;

    }

    @RequestMapping(value = "/activate_cid", method = RequestMethod.POST)
    public ResponseEntity<String> createActivationCode(@RequestBody WhitelistCodeRequest cid){
        if(whitelistService.isCIDWhiteListed(cid.getCid())) {
            Whitelist whitelist = whitelistService.getWhitelist(cid.getCid());
            String code = activationCodeService.generateActivationCode();
            ActivationCode activationCode = activationCodeService.saveActivationCode(whitelist, code);
            sendEmail(activationCode);
        }

        /*
        We always want to send the same response, for security reasons.
        If the responses would vary, brute force attacks could be made
        to find out real CID values.
         */
        return new WhitelistAddedResponse();
    }
    private void sendEmail(ActivationCode activationCode){
        String code = activationCode.getCode();
        String to = activationCode.getCid() + "@" + mailPostfix;
        String message = "Your code to Gamma is: " + code;
        mailSenderService.sendMail(to, "Chalmers activation code", message);
    }
}

