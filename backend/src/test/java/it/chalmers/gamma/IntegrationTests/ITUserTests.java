package it.chalmers.gamma.IntegrationTests;


import it.chalmers.gamma.TestUtils;
import it.chalmers.gamma.db.entity.Whitelist;
import it.chalmers.gamma.db.repository.ActivationCodeRepository;
import it.chalmers.gamma.db.repository.WhitelistRepository;
import it.chalmers.gamma.requests.CreateITUserRequest;
import it.chalmers.gamma.service.ITUserService;
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
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
        //      classes = GammaApplication.class
)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class ITUserTests {

    @Autowired
    ITUserService userService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    WhitelistRepository whitelistRepository;

    @Autowired
    ActivationCodeRepository activationCodeRepository;

    TestUtils utils;
    @Before

    public void setup(){
        utils = new TestUtils();
        utils.setMockMvc(mockMvc);
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
        userService.createUser(itUser1);
        userService.createUser(itUser2);

        MvcResult result =  mockMvc.perform
                (MockMvcRequestBuilders.get("/users/")
                        .accept(MediaType.APPLICATION_JSON)).andReturn();
        System.out.println(result.getResponse().getContentAsString());
        Assert.assertTrue(result.getResponse().getContentAsString().contains("gurr"));
        Assert.assertFalse(result.getResponse().getContentAsString().contains("sfe"));
    }
    @Test
    public void testCreateAccount() throws Exception {
        String cid = "TESTACC";
        utils.sendCreateCode(cid);
        CreateITUserRequest user = createAccount(cid);

        MockHttpServletRequestBuilder mocker = (MockMvcRequestBuilders.post("/users/create").content(utils.asJsonString(user)).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));

        mockMvc.perform(mocker).andDo(MockMvcResultHandlers.print());

        Assert.assertTrue(userService.userExists(cid));
        Assert.assertTrue(whitelistRepository.findByCid(cid) == null);
        Assert.assertTrue(activationCodeRepository.findByCid_Cid(cid) == null);
    }

    private CreateITUserRequest createAccount(String cid){
        CreateITUserRequest user = new CreateITUserRequest();
        user.setWhitelist(whitelistRepository.findByCid(cid));
        user.setCode(activationCodeRepository.findByCid_Cid(cid).getCode());
        user.setAcceptanceYear(2018);
        user.setFirstName("me");
        user.setLastName("alsome");
        user.setNick("it's a me");
        user.setPassword("examplepassword");
        user.setUserAgreement(true);
        return user;
    }

}
