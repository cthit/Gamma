package it.chalmers.gamma.integration;

import it.chalmers.gamma.TestUtils;
import it.chalmers.gamma.db.entity.ActivationCode;
import it.chalmers.gamma.db.entity.Whitelist;
import it.chalmers.gamma.db.repository.ActivationCodeRepository;
import it.chalmers.gamma.db.repository.WhitelistRepository;
import it.chalmers.gamma.jwt.JwtTokenProvider;
import it.chalmers.gamma.requests.CreateITUserRequest;
import it.chalmers.gamma.service.ActivationCodeService;
import it.chalmers.gamma.service.ITUserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class WhitelistTests {

    @Autowired
    WhitelistRepository whitelistRepository;

    @Autowired
    ActivationCodeService activationCodeService;

    @Autowired
    ActivationCodeRepository activationCodeRepository;

    static TestUtils utils;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    ITUserService userService;

    @Autowired
    MockMvc mockMvc;

    private static boolean hasRun = false;


    @Before
    public void setUp(){
        if(!hasRun) {
            utils = new TestUtils();
            utils.setMockMvc(mockMvc, jwtTokenProvider, userService);
            utils.addAdminUser();
            hasRun = true;
        }
    }

    @Test
    public void testCreateCode() {
        String cid = "TEST_CODE";
        this.utils.sendCreateCode(cid);
        Whitelist whitelist = this.whitelistRepository.findByCid(cid);
        Assert.assertTrue(this.activationCodeService.userHasCode(whitelist.getCid()));
    }

    @Test
    public void testExpiredCode() throws Exception {
        String cid = "expired";
        this.utils.sendCreateCode(cid);
        ActivationCode activationCode = this.activationCodeRepository.findByCid_Cid(cid);
        activationCode.setCreatedAt(activationCode.getCreatedAt().minusSeconds(2 * 3600 + 5));
        this.activationCodeRepository.save(activationCode);
        Assert.assertTrue(this.activationCodeService.hasCodeExpired(cid, 2));

    }

}


