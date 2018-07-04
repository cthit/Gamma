package it.chalmers.gamma;

import it.chalmers.gamma.exceptions.CIDAlreadyWhitelistedException;
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
    public void testWhiteList(){
        try {
            whitelistService.addWhiteListedCID("cid1");
            whitelistService.addWhiteListedCID("cid2");
        } catch (CIDAlreadyWhitelistedException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(whitelistService.isCIDWhiteListed("cid1"));
        Assert.assertFalse(whitelistService.isCIDWhiteListed("leif"));
    }
    @Test
    public void testDeleteActivationCode(){
        String cid = "cid1";
        try {
            whitelistService.addWhiteListedCID(cid);
            whitelistService.removeWhiteListedCID(cid);
        } catch (CIDAlreadyWhitelistedException e) {
            e.printStackTrace();
        }

    }


    /**
     * Tests if a CID can be added to the whitelisted database more than once.
     */
    @Test
    public void testAddWhitelistCIDMultipleTimes(){
        try {
            whitelistService.addWhiteListedCID("cid1");
        } catch (CIDAlreadyWhitelistedException e) {
            e.printStackTrace();
        }
        try {
            whitelistService.addWhiteListedCID("cid1");
            assert(false);
        } catch (CIDAlreadyWhitelistedException e) {
            e.printStackTrace();
            assert(true);
        }
    }
    /*
TODO Set up test environment that specifies mail address to send from and to.
 */
    @Test
    public void testSendEmail(){

    }
}