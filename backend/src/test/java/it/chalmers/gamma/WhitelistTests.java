package it.chalmers.gamma;

import it.chalmers.gamma.service.WhitelistService;

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
public class WhitelistTests {

    @Autowired
    WhitelistService whitelistService;

    /**
     * Tests if adding a CID to the whitelist works, and if seeing if a non-whitelisted CID returns correct result.
     */
    @Test
    public void testWhiteList() {
        String cid = "cid3";
        this.whitelistService.addWhiteListedCID(cid);
        this.whitelistService.addWhiteListedCID("cid4");
        Assert.assertTrue(this.whitelistService.isCIDWhiteListed(cid));
        Assert.assertFalse(this.whitelistService.isCIDWhiteListed("leif"));
    }

    @Test
    public void testDeleteActivationCode() {
        String cid = "cid5";
        this.whitelistService.addWhiteListedCID(cid);
        this.whitelistService.removeWhiteListedCID(cid);
        Assert.assertFalse(this.whitelistService.isCIDWhiteListed(cid));
    }


    /**
     * Tests if a CID can be added to the whitelisted database more than once.
     */
    @Test
    public void testAddWhitelistCIDMultipleTimes() {
        String cid = "cid1";
        try {
            this.whitelistService.addWhiteListedCID(cid);
            this.whitelistService.addWhiteListedCID(cid);
            Assert.fail();
        } catch (Exception ignored) {

        }
    }

    /*
     * TODO Set up test environment that specifies mail address to send from and to.
     */
    @Test
    public void testSendEmail() {
        Assert.assertTrue(true);
    }
}