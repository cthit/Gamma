package it.chalmers.gamma.api;

import it.chalmers.gamma.GammaApplication;
import it.chalmers.gamma.domain.dto.user.ActivationCodeDTO;
import it.chalmers.gamma.factories.MockActivationCodeFactory;
import it.chalmers.gamma.factories.MockITUserFactory;
import it.chalmers.gamma.factories.MockWhitelistFactory;
import it.chalmers.gamma.service.ActivationCodeService;
import it.chalmers.gamma.service.ITUserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = GammaApplication.class)
@ActiveProfiles("test")
public class ActivationCodeTests {


    private static final Logger LOGGER = LoggerFactory.getLogger(ActivationCodeTests.class);


    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private MockActivationCodeFactory mockActivationCodeFactory;

    @Autowired
    private MockWhitelistFactory mockWhitelistFactory;

    @Autowired
    private ActivationCodeService activationCodeService;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @WithUserDetails("admin")
    public void testDeleteActivationCode() throws Exception {
        ActivationCodeDTO activationCode = this.mockActivationCodeFactory.saveActivationCode(
                this.mockWhitelistFactory.saveWhitelist(mockWhitelistFactory.generateWhitelist()));
        mockMvc.perform(delete(String.format("/admin/activation_codes/%s", activationCode.getCid())))
                .andExpect(status().isAccepted());
        ActivationCodeDTO activationCode2 = this.mockActivationCodeFactory.saveActivationCode(
                this.mockWhitelistFactory.saveWhitelist(mockWhitelistFactory.generateWhitelist()));
        mockMvc.perform(delete(String.format("/admin/activation_codes/%s", activationCode2.getId())))
                .andExpect(status().isAccepted());
        Assert.assertFalse(this.activationCodeService.codeExists(activationCode.getCid()));
        Assert.assertFalse(this.activationCodeService.codeExists(activationCode2.getId().toString()));
    }
}
