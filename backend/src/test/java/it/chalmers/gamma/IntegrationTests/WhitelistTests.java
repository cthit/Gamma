package it.chalmers.gamma.IntegrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.chalmers.gamma.TestUtils;
import it.chalmers.gamma.db.entity.ActivationCode;
import it.chalmers.gamma.db.entity.Whitelist;
import it.chalmers.gamma.db.repository.ActivationCodeRepository;
import it.chalmers.gamma.db.repository.WhitelistRepository;
import it.chalmers.gamma.requests.CreateITUserRequest;
import it.chalmers.gamma.service.ActivationCodeService;
import it.chalmers.gamma.service.ITUserService;
import it.chalmers.gamma.service.WhitelistService;
import org.hibernate.annotations.Fetch;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;


@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
  //      classes = GammaApplication.class
)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class WhitelistTests {

    @Autowired
    WhitelistRepository whitelistRepository;

    @Autowired
    ActivationCodeService activationCodeService;

    @Autowired
    ActivationCodeRepository activationCodeRepository;

    TestUtils utils;

    @Autowired
    MockMvc mockMvc;

    @Before
    public void setup(){
        utils = new TestUtils(mockMvc);
    }

    @Test
    public void testCreateCode() throws Exception {
        String cid = "TEST_CODE";
        utils.sendCreateCode(cid);
        Whitelist whitelist = whitelistRepository.findByCid(cid);
        Assert.assertTrue(activationCodeService.userHasCode(whitelist.getCid()));
    }

    @Test
    public void testExpiredCode() throws Exception {
        String cid = "expired";
        utils.sendCreateCode(cid);
        ActivationCode activationCode = activationCodeRepository.findByCid_Cid(cid);
        activationCode.setCreatedAt(activationCode.getCreatedAt().minusSeconds((2*3600) + 5));
        activationCodeRepository.save(activationCode);
        Assert.assertTrue(activationCodeService.hasCodeExpired(cid, 2));

    }

}


