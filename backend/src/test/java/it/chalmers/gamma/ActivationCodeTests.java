package it.chalmers.gamma;

import it.chalmers.gamma.db.entity.ActivationCode;
import it.chalmers.gamma.db.entity.Whitelist;
import it.chalmers.gamma.db.repository.ActivationCodeRepository;
import it.chalmers.gamma.db.repository.WhitelistRepository;
import it.chalmers.gamma.service.ActivationCodeService;
import it.chalmers.gamma.service.WhitelistService;

import it.chalmers.gamma.util.TokenUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class ActivationCodeTests {

    @Autowired
    ActivationCodeService activationCodeService;

    @Autowired
    ActivationCodeRepository activationCodeRepository;

    @Autowired
    WhitelistRepository whitelistRepository;

    @Autowired
    WhitelistService whitelistService;

  /*  @Test
    public void testAddActivationCode() {
        String cid = "cid3";
        Whitelist whitelist = this.whitelistRepository.save(new Whitelist(cid));
        ActivationCode activationCode = this.activationCodeService
                .saveActivationCode(whitelist,
                        TokenUtils.generateToken(15, TokenUtils.CharacterTypes.allValues()));
        Assert.assertTrue(this.activationCodeRepository.findByCid_Cid(cid).equals(activationCode));
    }

    //TODO how do we test this?
    @Test
    public void testActivationCode() {
        Assert.assertTrue(true);
    }

    @Test
    public void testDeleteActivationCode() {
        String cid = "cid4";
        Whitelist whitelist = this.whitelistRepository.save(new Whitelist(cid));
        ActivationCode activationCode = this.activationCodeService
                .saveActivationCode(whitelist,
                        TokenUtils.generateToken(15, TokenUtils.CharacterTypes.allValues()));
        this.activationCodeService.deleteCode(whitelist.getCid());
        this.whitelistService.removeWhiteListedCID(activationCode.getCid());
        Assert.assertFalse(this.activationCodeRepository.existsActivationCodeByCid_Cid(cid));
    }*/
}
