package it.chalmers.gamma.controller;

import it.chalmers.gamma.db.entity.ActivationCode;
import it.chalmers.gamma.db.entity.Whitelist;
import it.chalmers.gamma.exceptions.CIDAlreadyWhitelistedException;
import it.chalmers.gamma.exceptions.NoCidFoundException;
import it.chalmers.gamma.exceptions.UserAlreadyExistsException;
import it.chalmers.gamma.service.ActivationCodeService;
import it.chalmers.gamma.service.ITUserService;
import it.chalmers.gamma.service.MailSenderService;
import it.chalmers.gamma.service.WhitelistService;
import org.h2.engine.User;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/whitelist", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class WhitelistController {

    private WhitelistService whitelistService;

    private ActivationCodeService activationCodeService;

    private ITUserService itUserService;

    private MailSenderService mailSenderService;

    private String mailPostfix = "@student.chalmers.se";

    public WhitelistController(WhitelistService whitelistService, ActivationCodeService activationCodeService, ITUserService itUserService, MailSenderService mailSenderService){
        this.whitelistService = whitelistService;
        this.activationCodeService = activationCodeService;
        this.itUserService = itUserService;
        this.mailSenderService = mailSenderService;

    }
    @GetMapping
    public List<Whitelist> getAllWhiteListed(){
        return whitelistService.findAll();
    }

    /**
     * /whitelist/valid will be able to return whether or not a user is whitelist, without doing anything to modify the data.
     * @param cid CID of a user.
     * @return true if the user is whitelisted false otherwise
     */
    @RequestMapping(value = "/valid", method = RequestMethod.POST)
    public boolean isValid(@RequestBody Whitelist cid) {
        System.out.println(cid);
        return whitelistService.isCIDWhiteListed(cid.getCid());
    }
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public void addUser(@RequestBody Whitelist cid) throws CIDAlreadyWhitelistedException, UserAlreadyExistsException {
        if (whitelistService.isCIDWhiteListed(cid.getCid())) {
            throw new CIDAlreadyWhitelistedException();
        }
        if (itUserService.userExists(cid.getCid())) {
            throw new UserAlreadyExistsException();
        }
        whitelistService.addWhiteListedCID(cid.getCid());
    }
    //TODO should probably return something to tell the backend whether or not creating the account was successful.
    @RequestMapping(value = "/activate_cid", method = RequestMethod.POST)
    public boolean createActivationCode(@RequestBody Whitelist cid) throws UserAlreadyExistsException, NoCidFoundException {
        if(itUserService.userExists(cid.getCid())){
            throw new UserAlreadyExistsException();
        }
        if(whitelistService.isCIDWhiteListed(cid.getCid())) {
            Whitelist whitelist = whitelistService.findByCid(cid.getCid());
            String code = activationCodeService.generateActivationCode();
            ActivationCode activationCode = activationCodeService.saveActivationCode(whitelist, code);
       //     sendEmail(activationCode);
            return true;
        }
        else
            throw new NoCidFoundException();
    }
    private void sendEmail(ActivationCode activationCode){
        String code = activationCode.getCode();
        String to = activationCode.getCid() + mailPostfix;
        String message = "Your code to Gamma is: " + code;
        try {
            mailSenderService.sendMessage(to, "login code", message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}

