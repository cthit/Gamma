package it.chalmers.gamma.service;

import it.chalmers.gamma.service.WhitelistService;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class WhitelistServiceTests {

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
            Assert.assertTrue(true);
        }
    }

}