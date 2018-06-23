package it.chalmers.gamma.IntegrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.chalmers.gamma.db.entity.Whitelist;
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
public class WhitelistTable {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ITUserService userService;

    @Autowired
    ActivationCodeService activationCodeService;

    @Autowired
    WhitelistService whitelistService;



    @Test
    public void testCreateCode() throws Exception {
        String cid = "account";
        MockHttpServletRequestBuilder mocker = (MockMvcRequestBuilders.post("/whitelist/add/").content(asJsonString(new Whitelist(cid))).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));
        MockHttpServletRequestBuilder mocker2 = (MockMvcRequestBuilders.post("/whitelist/add/").content(asJsonString(new Whitelist(cid + "1"))).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));
        MockHttpServletRequestBuilder mocker3 = (MockMvcRequestBuilders.post("/whitelist/add/").content(asJsonString(new Whitelist(cid + "2"))).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));

        MockHttpServletRequestBuilder mocker4 = (MockMvcRequestBuilders.post("/whitelist/activate_cid/").content(asJsonString(new Whitelist(cid))).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));


        mockMvc.perform(mocker);
        mockMvc.perform(mocker4).andDo(MockMvcResultHandlers.print());
        Whitelist whitelist = whitelistService.findByCid(cid);
        Assert.assertTrue(activationCodeService.userHasCode(whitelist));
    }

    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*
    TODO Set up test environment that specifies mail address to send from and to.
     */
    @Test
    public void testSendEmail(){

    }
}
