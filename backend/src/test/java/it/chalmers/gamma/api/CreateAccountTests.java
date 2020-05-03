package it.chalmers.gamma.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.chalmers.gamma.GammaApplication;
import it.chalmers.gamma.domain.dto.user.ActivationCodeDTO;
import it.chalmers.gamma.domain.dto.user.WhitelistDTO;
import it.chalmers.gamma.factories.MockITUserFactory;
import it.chalmers.gamma.factories.MockWhitelistFactory;
import it.chalmers.gamma.service.ActivationCodeService;
import it.chalmers.gamma.service.ITUserService;
import it.chalmers.gamma.utils.JSONUtils;
import java.util.Objects;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = GammaApplication.class)
@ActiveProfiles("test")
@SuppressWarnings({"PMD.JUnitTestsShouldIncludeAssert"})
public class CreateAccountTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private MockWhitelistFactory mockWhitelistFactory;

    @Autowired
    private MockITUserFactory mockITUserFactory;

    @Autowired
    private ActivationCodeService activationCodeService;

    @Autowired
    private ITUserService userService;

    @Before
    public void setupTests() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    public void testCreateActivationCodeWhitelistedAccount() throws Exception {
        WhitelistDTO whitelist = this.mockWhitelistFactory.saveWhitelist(this.mockWhitelistFactory.generateWhitelist());
        this.testCreateActivationCode(whitelist, true);
    }

    @Test
    public void testCreateActivationCodeNonWhitelistedAccount() throws Exception {
        this.testCreateActivationCode(this.mockWhitelistFactory.generateWhitelist(), false);
    }

    private void testCreateActivationCode(WhitelistDTO whitelist, boolean shouldCreate) throws Exception {
        this.mockMvc.perform(post("/whitelist/activate_cid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(JSONUtils.objectToJSONString(
                        this.mockWhitelistFactory.createValidRequest(whitelist)))))
                .andExpect(status().is(202));       // To hide if user exists, we always return OK
        Assert.assertEquals(this.activationCodeService.codeExists(whitelist.getCid()), shouldCreate);
    }

    @Test
    public void testCreateAccountValidCode() throws Exception {
        WhitelistDTO whitelist = this.mockWhitelistFactory.saveWhitelist(this.mockWhitelistFactory.generateWhitelist());
        this.testCreateActivationCode(whitelist, true);
        ActivationCodeDTO activationCodeDTO = this.activationCodeService.getActivationCodeDTO(whitelist.getCid());
        this.mockMvc.perform(post("/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(JSONUtils.objectToJSONString(
                        this.mockITUserFactory.createValidCreateRequest(
                                this.mockITUserFactory.generateITUser(
                                        "user",
                                        true),
                                activationCodeDTO))))).andExpect(status().isAccepted());
        Assert.assertTrue(this.userService.userExists(whitelist.getCid()));
    }
}
