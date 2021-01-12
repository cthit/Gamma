package it.chalmers.gamma.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.chalmers.gamma.GammaApplication;
import it.chalmers.gamma.whitelist.WhitelistDTO;
import it.chalmers.gamma.factories.MockActivationCodeFactory;
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

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = GammaApplication.class)
@ActiveProfiles("test")
@SuppressWarnings({"PMD.JUnitTestsShouldIncludeAssert"})
public class WhitelistTests {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private MockWhitelistFactory mockWhitelistFactory;

    @Autowired
    private MockActivationCodeFactory mockActivationCodeFactory;

    private static final String WHITELIST_URL = "/admin/users/whitelist/%s";

    @Before
    public void setupTests() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    /**
     * Tests first to delete whitelist that has valid activationcode code using the id, then the cid.
     * @throws Exception if mockMvc fails.
     */
    @WithUserDetails("admin")
    @Test
    public void testDeleteWhitelistWithActiveActivationCode() throws Exception {
        WhitelistDTO whitelist = this.mockWhitelistFactory.saveWhitelist(
                this.mockWhitelistFactory.generateWhitelist());
        this.mockActivationCodeFactory.saveActivationCode(whitelist);

        this.mockMvc.perform(delete(String.format(WHITELIST_URL, whitelist.getId())))
                .andExpect(status().isOk());

        WhitelistDTO whitelist2 = this.mockWhitelistFactory.saveWhitelist(
                this.mockWhitelistFactory.generateWhitelist());
        this.mockActivationCodeFactory.saveActivationCode(whitelist2);

        this.mockMvc.perform(delete(String.format(WHITELIST_URL, whitelist2.getCid())))
                .andExpect(status().isOk());
    }

    /**
     * Tests first to delete whitelist that does not hav a activationcode code using the id, then the cid.
     * @throws Exception if mockMvc fails.
     */
    @WithUserDetails("admin")
    @Test
    public void testDeleteWhitelistWithNoActiveActivationCode() throws Exception {
        WhitelistDTO whitelist = this.mockWhitelistFactory.saveWhitelist(
                this.mockWhitelistFactory.generateWhitelist());

        this.mockMvc.perform(delete(String.format(WHITELIST_URL, whitelist.getId())))
                .andExpect(status().isOk());

        WhitelistDTO whitelist2 = this.mockWhitelistFactory.saveWhitelist(
                this.mockWhitelistFactory.generateWhitelist());

        this.mockMvc.perform(delete(String.format(WHITELIST_URL, whitelist2.getCid())))
                .andExpect(status().isOk());
    }

}
