package it.chalmers.gamma.service;

import it.chalmers.gamma.domain.dto.user.WhitelistDTO;
import it.chalmers.gamma.factories.MockActivationCodeFactory;
import it.chalmers.gamma.factories.MockWhitelistFactory;
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
public class ActivationCodeServiceTests {

    @Autowired
    private MockActivationCodeFactory mockActivationCodeFactory;

    @Autowired
    private MockWhitelistFactory mockWhitelistFactory;

    @Autowired
    private ActivationCodeService activationCodeService;

    // This
    @Test
    public void testCreateMultipleActivationCodes() {
        WhitelistDTO whitelist = this.mockWhitelistFactory.saveWhitelist(this.mockWhitelistFactory.generateWhitelist());
        this.mockActivationCodeFactory.saveActivationCode(whitelist);
        this.mockActivationCodeFactory.saveActivationCode(whitelist);
        Assert.assertTrue(this.activationCodeService.codeExists(whitelist.getCid()));
    }


}
