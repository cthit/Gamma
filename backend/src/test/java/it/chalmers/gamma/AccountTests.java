package it.chalmers.gamma;

import it.chalmers.gamma.exceptions.CIDAlreadyWhitelistedException;
import it.chalmers.gamma.service.WhitelistService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountTests {

    @Autowired
    WhitelistService whitelistService;

    /**
     * Tests if adding a CID to the whitelist works, and if seeing if a non-whitelisted CID returns correct result.
     */
    @Test
    public void testWhiteList(){
        try {
            whitelistService.addWhiteListedCID("engsmyre");
            whitelistService.addWhiteListedCID("svenel");
        } catch (CIDAlreadyWhitelistedException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(whitelistService.isCIDWhiteListed("engsmyre"));
        Assert.assertFalse(whitelistService.isCIDWhiteListed("leif"));
    }


    /**
     * Tests if a CID can be added to the whitelisted database more than once.
     */
    @Test
    public void testAddWhitelistCIDMultipleTimes(){
        try {
            whitelistService.addWhiteListedCID("engsmyre");
        } catch (CIDAlreadyWhitelistedException e) {
            e.printStackTrace();
        }
        try {
            whitelistService.addWhiteListedCID("engsmyre");
            assert(false);
        } catch (CIDAlreadyWhitelistedException e) {
            e.printStackTrace();
            assert(true);
        }
    }
}