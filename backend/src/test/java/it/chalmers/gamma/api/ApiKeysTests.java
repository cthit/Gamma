package it.chalmers.gamma.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.chalmers.gamma.GammaApplication;
import it.chalmers.gamma.domain.dto.access.ApiKeyDTO;
import it.chalmers.gamma.factories.MockApiKeyFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
public class ApiKeysTests {


    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private MockApiKeyFactory apiKeyFactory;

    @Before
    public void setupTests() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    public void testAccessWithValidAPIKey() throws Exception {
        ApiKeyDTO apiKey = this.apiKeyFactory.saveApiKey(this.apiKeyFactory.generateApiKey());
        this.mockMvc.perform(get("/admin/users").header("authorization",
                String.format("pre-shared %s", apiKey.getKey()))).andExpect(status().isOk());
    }

    @Test
    public void testAccessWithInvalidAPIKey() throws Exception {
        ApiKeyDTO apiKey = this.apiKeyFactory.generateApiKey();
        this.mockMvc.perform(get("/admin/users").header("authorization",
                String.format("pre-shared %s", apiKey.getKey()))).andExpect(status().isUnauthorized());
    }
}
