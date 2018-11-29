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
import it.chalmers.gamma.util.TokenUtils;

import java.time.Year;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class ITUserIntegrationTests {

    @Autowired
    ITUserService userService;

    @Autowired
    ActivationCodeService activationCodeService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    WhitelistRepository whitelistRepository;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    ActivationCodeRepository activationCodeRepository;

    TestUtils utils;

    @Before
    public void setup() {
        this.utils = new TestUtils();
        this.utils.setMockMvc(this.mockMvc);
    }

    @Test
    public void testDisplayUsers() throws Exception {
        CreateITUserRequest itUser1 = new CreateITUserRequest();
        itUser1.setNick("gurr");
        itUser1.setPassword("examplePassword");
        itUser1.setWhitelist(new Whitelist("example"));
        CreateITUserRequest itUser2 = new CreateITUserRequest();
        itUser2.setNick("leif");
        itUser2.setPassword("examplePassword");
        itUser2.setWhitelist(new Whitelist("example2"));
        this.userService.createUser(itUser1.getNick(), itUser1.getFirstName(),
                itUser1.getLastName(), itUser1.getWhitelist().getCid(),
                Year.of(itUser1.getAcceptanceYear()), itUser1.isUserAgreement(),
                null, itUser1.getPassword()
        );
        this.userService.createUser(itUser2.getNick(), itUser2.getFirstName(),
                itUser2.getLastName(), itUser2.getWhitelist().getCid(),
                Year.of(itUser2.getAcceptanceYear()), itUser2.isUserAgreement(),
                null, itUser2.getPassword()
        );

        String token = this.jwtTokenProvider.createToken("gurr");
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                .get("/users/")
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)).andReturn();

        Assert.assertTrue(result.getResponse().getContentAsString().contains("gurr"));
        Assert.assertFalse(result.getResponse().getContentAsString().contains("sfe"));
    }

    @Test
    public void testCreateAccount() throws Exception {
        String cid = "TESTACC";
        this.utils.sendCreateCode(cid);

        Whitelist wl = new Whitelist(cid);
        this.whitelistRepository.save(wl);
        ActivationCode activationCode = new ActivationCode(wl);
        activationCode.setCode(TokenUtils.generateToken());
        this.activationCodeService.saveActivationCode(wl, activationCode.getCode());

        CreateITUserRequest user = createAccount(cid);

        MockHttpServletRequestBuilder mocker = MockMvcRequestBuilders
                .post("/users/create")
                .content(this.utils.asJsonString(user))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(mocker).andDo(MockMvcResultHandlers.print());

        Assert.assertTrue(this.userService.userExists(cid));
        Assert.assertNull(this.whitelistRepository.findByCid(cid));
        Assert.assertNull(this.activationCodeRepository.findByCid_Cid(cid));
    }

    private CreateITUserRequest createAccount(String cid) {
        CreateITUserRequest user = new CreateITUserRequest();
        user.setWhitelist(this.whitelistRepository.findByCid(cid));
        user.setCode(this.activationCodeRepository.findByCid_Cid(cid).getCode());
        user.setAcceptanceYear(2018);
        user.setFirstName("me");
        user.setLastName("alsome");
        user.setNick("it's a me");
        user.setPassword("examplepassword");
        user.setUserAgreement(true);
        return user;
    }

}
