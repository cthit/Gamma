package it.chalmers.gamma.controller;

import it.chalmers.gamma.db.entity.ActivationCode;
import it.chalmers.gamma.db.entity.Whitelist;
import it.chalmers.gamma.response.CIDAlreadyWhitelistedResponse;
import it.chalmers.gamma.response.NoCidFoundResponse;
import it.chalmers.gamma.response.UserAddedResponse;
import it.chalmers.gamma.response.UserAlreadyExistsResponse;
import it.chalmers.gamma.requests.WhitelistCodeRequest;
import it.chalmers.gamma.service.ActivationCodeService;
import it.chalmers.gamma.service.ITUserService;
import it.chalmers.gamma.service.MailSenderService;
import it.chalmers.gamma.service.WhitelistService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

@RestController
@RequestMapping(value = "/whitelist", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class WhitelistController {

    private WhitelistService whitelistService;

    private ActivationCodeService activationCodeService;

    private ITUserService itUserService;

    private MailSenderService mailSenderService;

    @Value("${mail.receiver.standard-postfix}")
    private String mailPostfix;

    public WhitelistController(WhitelistService whitelistService, ActivationCodeService activationCodeService, ITUserService itUserService, MailSenderService mailSenderService){
        this.whitelistService = whitelistService;
        this.activationCodeService = activationCodeService;
        this.itUserService = itUserService;
        this.mailSenderService = mailSenderService;

    }

    /**
     * /whitelist/valid will be able to return whether or not a user is whitelist, without doing anything to modify the data.
     * @param cid CID of a user.
     * @return true if the user is whitelisted false otherwise
     */
    @RequestMapping(value = "/valid", method = RequestMethod.POST)
    public boolean isValid(@RequestBody WhitelistCodeRequest cid) {
        return whitelistService.isCIDWhiteListed(cid.getCid());
    }

    @RequestMapping(value = "/activate_cid", method = RequestMethod.POST)
    public ResponseEntity<String> createActivationCode(@RequestBody WhitelistCodeRequest cid){
        if(itUserService.userExists(cid.getCid())){
            return new UserAlreadyExistsResponse();
        }
        if(whitelistService.isCIDWhiteListed(cid.getCid())) {
            Whitelist whitelist = whitelistService.getWhitelist(cid.getCid());
            String code = activationCodeService.generateActivationCode();
            ActivationCode activationCode = activationCodeService.saveActivationCode(whitelist, code);
            //sendEmail(activationCode);
            return new UserAddedResponse();
        }
        else
            return new NoCidFoundResponse();
    }
    private void sendEmail(ActivationCode activationCode){
        String code = activationCode.getCode();
        String to = activationCode.getCid() + "@" + mailPostfix;
        String message = "Your code to Gamma is: " + code;
        try {
            mailSenderService.sendMessage(to, "login code", message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}

