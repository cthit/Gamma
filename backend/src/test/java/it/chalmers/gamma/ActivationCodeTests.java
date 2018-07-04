package it.chalmers.gamma;

import it.chalmers.gamma.db.entity.ActivationCode;
import it.chalmers.gamma.db.entity.Whitelist;
import it.chalmers.gamma.db.repository.ActivationCodeRepository;
import it.chalmers.gamma.db.repository.WhitelistRepository;
import it.chalmers.gamma.service.ActivationCodeService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class ActivationCodeTests {

    @Autowired
    ActivationCodeService activationCodeService;

    @Autowired
    ActivationCodeRepository activationCodeRepository;

    @Autowired
    WhitelistRepository whitelistRepository;

    @Test
    public void testAddActivationCode(){
        String cid = "cid3";
        Whitelist whitelist = whitelistRepository.save(new Whitelist(cid));
        ActivationCode activationCode = activationCodeService.
                saveActivationCode(whitelist, activationCodeService.generateActivationCode());
        Assert.assertTrue(activationCodeRepository.findByCid_Cid(cid).equals(activationCode));
    }

    //TODO how do we test this?
    @Test
    public void testActivationCode(){

    }

    @Test
    public void testDeleteActivationCode(){
        String cid = "cid4";
        Whitelist whitelist = whitelistRepository.save(new Whitelist(cid));
        ActivationCode activationCode = activationCodeService.
                saveActivationCode(whitelist, activationCodeService.generateActivationCode());
        activationCodeService.deleteCode(whitelist);
        System.out.println(activationCodeRepository.findAll());
        Assert.assertFalse(activationCodeRepository.existsActivationCodeByCid_Cid(cid));
    }
}
