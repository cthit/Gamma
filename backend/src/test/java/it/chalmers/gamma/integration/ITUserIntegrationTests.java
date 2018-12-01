package it.chalmers.gamma.integration;


import it.chalmers.gamma.TestUtils;

import it.chalmers.gamma.db.entity.ActivationCode;
import it.chalmers.gamma.db.entity.Whitelist;
import it.chalmers.gamma.db.repository.ActivationCodeRepository;
import it.chalmers.gamma.db.repository.WhitelistRepository;
import it.chalmers.gamma.jwt.JwtTokenProvider;
import it.chalmers.gamma.requests.CidPasswordRequest;
import it.chalmers.gamma.requests.CreateITUserRequest;
import it.chalmers.gamma.service.ActivationCodeService;
import it.chalmers.gamma.service.ITUserService;
import it.chalmers.gamma.service.WhitelistService;
import org.json.simple.JSONObject;
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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.servlet.resource.HttpResource;

import javax.validation.constraints.AssertTrue;
import java.time.Year;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
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

    static TestUtils utils;

    private static boolean hasRun = false;

    @Before
    public void setup(){
        if(!hasRun) {
            utils = new TestUtils();
            utils.setMockMvc(mockMvc, jwtTokenProvider, userService);
            hasRun = true;
        }
    }

    @Test
    public void testDisplayUsers() throws Exception {
        String nick1 = "gurr";
        String cid1 = "example";
        String password = "examplePassword";
        CreateITUserRequest itUser1 = new CreateITUserRequest();
        itUser1.setNick(nick1);
        itUser1.setPassword(password);
        itUser1.setWhitelist(new Whitelist(cid1));
        CreateITUserRequest itUser2 = new CreateITUserRequest();
        itUser2.setNick("leif");
        itUser2.setPassword(password);
        itUser2.setWhitelist(new Whitelist("example2"));
        userService.createUser(itUser1.getNick(), itUser1.getFirstName(), itUser1.getLastName(), itUser1.getWhitelist().getCid(), Year.of(itUser1.getAcceptanceYear()), itUser1.isUserAgreement(), null, itUser1.getPassword());
        userService.createUser(itUser2.getNick(), itUser2.getFirstName(), itUser2.getLastName(), itUser2.getWhitelist().getCid(), Year.of(itUser2.getAcceptanceYear()), itUser2.isUserAgreement(), null, itUser2.getPassword());
        String token = jwtTokenProvider.createToken(cid1);
        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.get("/users/" + cid1).header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)).andReturn();
        Assert.assertTrue(result.getResponse().getContentAsString().contains(nick1));

        Assert.assertTrue(result.getResponse().getContentAsString().contains(cid1));
        Assert.assertFalse(result.getResponse().getContentAsString().contains("sfe"));
    }

    @Test
    public void testCreateAccount() throws Exception {
        String cid = "TESTACC";
        utils.sendCreateCode(cid);
        Whitelist whitelist = whitelistRepository.findByCid(cid);
        String activationCode = activationCodeRepository.findByCid_Cid(cid).getCode();
        CreateITUserRequest user = new CreateITUserRequest();
        user.setCode(activationCode);
        user.setWhitelist(whitelist);
        user.setPassword("password");
        MockHttpServletRequestBuilder mocker = MockMvcRequestBuilders
                .post("/users/create")
                .content(utils.asJsonString(user))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(mocker);

        Assert.assertTrue(this.userService.userExists(cid));
        Assert.assertNull(this.whitelistRepository.findByCid(cid));
        Assert.assertNull(this.activationCodeRepository.findByCid_Cid(cid));
    }
    @Test
    public void testLogin() throws Exception {
        String cid = "testlogin";
        String password = "password";
        userService.createUser("", "", "", cid, Year.of(2018), false, "", password);
        JSONObject loginData = new JSONObject();
        loginData.put("cid", cid);
        loginData.put("password", password);

        MockHttpServletRequestBuilder mocker = MockMvcRequestBuilders.post("/users/login")
                .content(loginData.toJSONString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(mocker).andReturn();
        String token = result.getResponse().getContentAsString();
        Assert.assertTrue(jwtTokenProvider.validateToken(token));
    }
    @Test
    public void testGetMe() throws Exception {
        String cid = "testme";
        CreateITUserRequest request = createAccount(cid);
        String token = jwtTokenProvider.createToken(cid);
        MockHttpServletRequestBuilder mocker = MockMvcRequestBuilders.get("/users/me")
                .header("Authorization", "Bearer " + token);
        MvcResult result = mockMvc.perform(mocker).andDo(MockMvcResultHandlers.print()).andReturn();
        Assert.assertTrue(result.getResponse().getContentAsString().contains(cid));
    }

    private CreateITUserRequest createAccount(String cid){     // Rewrite this.
        CreateITUserRequest user = new CreateITUserRequest();
        Whitelist whitelist;
        ActivationCode activationCode;
        if(whitelistRepository.findByCid(cid) == null) {
            whitelist = new Whitelist(cid);
            whitelistRepository.save(whitelist);
            activationCode = new ActivationCode(whitelist);
            activationCodeRepository.save(activationCode);
        }
        else {
            whitelist = whitelistRepository.findByCid(cid);
            activationCode = activationCodeRepository.findByCid_Cid(cid);
        }

        user.setWhitelist(whitelist);
        user.setCode(activationCode.getCode());
        user.setAcceptanceYear(2018);
        user.setFirstName("me");
        user.setLastName("alsome");
        user.setNick("it's a me");
        user.setPassword("examplepassword");
        user.setUserAgreement(true);
        userService.createUser(user.getNick(), user.getFirstName(), user.getLastName(), cid, Year.of(user.getAcceptanceYear()), user.isUserAgreement(), null, user.getPassword());
        return user;
    }

}
