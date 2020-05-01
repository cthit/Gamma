package it.chalmers.gamma.api;

import it.chalmers.gamma.GammaApplication;
import it.chalmers.gamma.domain.dto.user.WhitelistDTO;
import it.chalmers.gamma.factories.MockActivationCodeFactory;
import it.chalmers.gamma.factories.MockITClientFactory;
import it.chalmers.gamma.factories.MockWhitelistFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
public class WhitelistTests {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private MockWhitelistFactory mockWhitelistFactory;

    @Autowired
    private MockActivationCodeFactory mockActivationCodeFactory;

    @Before
    public void setupTests() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @WithUserDetails("admin")
    @Test
    public void testDeleteWhitelistWithActiveActivationCode() throws Exception {
        WhitelistDTO whitelist = this.mockWhitelistFactory.saveWhitelist(this.mockWhitelistFactory.generateWhitelist());
        mockActivationCodeFactory.saveActivationCode(whitelist);
        this.mockMvc.perform(delete(String.format("/admin/users/whitelist/%s", whitelist.getId())))
                .andExpect(status().isAccepted());
    }

    @WithUserDetails("admin")
    @Test
    public void testDeleteWhitelistWithNoActiveActivationCode() throws Exception {
        WhitelistDTO whitelist = this.mockWhitelistFactory.saveWhitelist(this.mockWhitelistFactory.generateWhitelist());
        this.mockMvc.perform(delete(String.format("/admin/users/whitelist/%s", whitelist.getId())))
                .andExpect(status().isAccepted());
    }

}
