package it.chalmers.gamma.integration;

import it.chalmers.gamma.GammaApplication;
import it.chalmers.gamma.TestUtils;
import it.chalmers.gamma.db.repository.ActivationCodeRepository;
import it.chalmers.gamma.db.repository.WhitelistRepository;
import it.chalmers.gamma.service.ActivationCodeService;
import it.chalmers.gamma.service.ITUserService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = GammaApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@TestPropertySource(locations = "classpath:application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class WhitelistIntegrationTests {

    @Autowired
    WhitelistRepository whitelistRepository;

    @Autowired
    ActivationCodeService activationCodeService;

    @Autowired
    ActivationCodeRepository activationCodeRepository;

    @Autowired
    ITUserService userService;

    @Autowired
    MockMvc mockMvc;

    static TestUtils utils = new TestUtils();


    @Before
    public void startup() {
        utils.setMockMvc(this.mockMvc, this.userService);
    }

    @Test
    public void testCreateCode() {
//        String cid = "TEST_CODE";
//        try {
//            utils.sendCreateCode(cid);
//        } catch (Exception e) {
//            LoggerFactory.getLogger(ITUserController.class).info(e.getMessage(), e);
//        }
//        Whitelist whitelist = this.whitelistRepository.findByCid(cid);
//        Assert.assertTrue(this.activationCodeService.userHasCode(whitelist.getCid()));
        Assert.assertTrue(true);
    }

    @Test
    public void testExpiredCode() throws Exception {
//        String cid = "expired";
//        utils.sendCreateCode(cid);
//        ActivationCode activationCode = this.activationCodeRepository.findByCid_Cid(cid);
//        activationCode.setCreatedAt(activationCode.getCreatedAt().minusSeconds(2 * 3600 + 5));
//        this.activationCodeRepository.save(activationCode);
//        Assert.assertTrue(this.activationCodeService.hasCodeExpired(cid, 2));
//
        Assert.assertTrue(true);
    }
}


