package it.chalmers.gamma.IntegrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.chalmers.gamma.db.entity.Whitelist;
import it.chalmers.gamma.db.repository.ActivationCodeRepository;
import it.chalmers.gamma.db.repository.WhitelistRepository;
import it.chalmers.gamma.requests.CreateITUserRequest;
import it.chalmers.gamma.service.ActivationCodeService;
import it.chalmers.gamma.service.ITUserService;
import it.chalmers.gamma.service.WhitelistService;
import org.hibernate.annotations.Fetch;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
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


@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
  //      classes = GammaApplication.class
)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class WhitelistTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ITUserService userService;

    @Autowired
    ActivationCodeService activationCodeService;

    @Autowired
    WhitelistService whitelistService;

    @Autowired
    ActivationCodeRepository activationCodeRepository;

    @Autowired
    WhitelistRepository whitelistRepository;

    @Test
    public void testCreateCode() throws Exception {
        String cid = "TEST_CODE";
        sendCreateCode(cid);
        Whitelist whitelist = whitelistRepository.findByCid(cid);
        Assert.assertTrue(activationCodeService.userHasCode(whitelist.getCid()));
    }

    private void sendCreateCode(String cid) throws Exception {
        MockHttpServletRequestBuilder mocker = (MockMvcRequestBuilders.post("/whitelist/add/").content(asJsonString(new Whitelist(cid))).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));
        MockHttpServletRequestBuilder mocker4 = (MockMvcRequestBuilders.post("/whitelist/activate_cid/").content(asJsonString(new Whitelist(cid))).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));

        mockMvc.perform(mocker);
        mockMvc.perform(mocker4).andDo(MockMvcResultHandlers.print());
    }

    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    public void testCreateAccount() throws Exception {
        String cid = "TESTACC";
        sendCreateCode(cid);
        CreateITUserRequest user = new CreateITUserRequest();
        System.out.println(whitelistRepository.findAll());
        Whitelist whitelist = whitelistRepository.findByCid(cid);
        user.setWhitelist(whitelistRepository.findByCid(cid));
        System.out.println(whitelist);
        System.out.println(activationCodeRepository.findByCid_Cid(cid));
        user.setCode(activationCodeRepository.findByCid_Cid(cid).getCode());
        user.setAcceptanceYear(2018);
        user.setFirstName("me");
        user.setLastName("alsome");
        user.setNick("it's a me");
        user.setPassword("examplepassword");
        user.setUserAgreement(true);

        MockHttpServletRequestBuilder mocker = (MockMvcRequestBuilders.post("/users/create").content(asJsonString(user)).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));

        mockMvc.perform(mocker).andDo(MockMvcResultHandlers.print());

        Assert.assertTrue(userService.userExists(cid));
        activationCodeRepository.deleteAll();
        whitelistRepository.deleteAll();
        }
}


