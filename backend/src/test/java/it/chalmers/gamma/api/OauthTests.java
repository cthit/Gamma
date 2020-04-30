package it.chalmers.gamma.api;

import it.chalmers.gamma.Endoints.JSONParameter;
import it.chalmers.gamma.GammaApplication;
import it.chalmers.gamma.domain.dto.access.ITClientDTO;
import it.chalmers.gamma.factories.MockITClientFactory;
import it.chalmers.gamma.utils.GenerationUtils;
import it.chalmers.gamma.utils.GenerationUtils.CharacterTypes;
import it.chalmers.gamma.utils.JSONUtils;
import java.util.Objects;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.w3c.dom.ls.LSOutput;

import static it.chalmers.gamma.utils.GenerationUtils.CharacterTypes.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = GammaApplication.class)
@ActiveProfiles("test")
public class OauthTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private MockITClientFactory mockITClientFactory;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    /**
     * Tests if going to oauth endpoints as non-authenticated redirects to login page
     * @throws Exception
     */
    @Test
    public void testLoginRedirect() throws Exception {
        this.mockMvc.perform(
               get("/oauth")
        ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
        .andDo(print());
    }

    // This will fail right now.
    @WithMockUser
    @Test
    public void testAuthorizationRequestWithIncorrectId() throws Exception {
        ITClientDTO clientDTO = this.mockITClientFactory
                .saveClient(this.mockITClientFactory.generateClient("https://test.com/auth"));

        String query = getTestAuthorizationQuery(GenerationUtils.generateRandomString(40, GenerationUtils.CharacterTypes.LOWERCASE),
                clientDTO.getWebServerRedirectUri());
        this.mockMvc.perform(get(query))
                .andExpect(status().is(422))
                .andExpect(status().reason("NO_SUCH_CLIENT_EXISTS"));
    }

    @WithMockUser
    @Test
    public void testAuthorizationRequestWithIncorrectRedirect() throws Exception {
        ITClientDTO clientDTO = this.mockITClientFactory
                .saveClient(this.mockITClientFactory.generateClient("https://test.com/auth"));

        String query = getTestAuthorizationQuery(clientDTO.getClientId(),
                GenerationUtils.generateRandomString(40, LOWERCASE));
        this.mockMvc.perform(get(query)).andExpect(status().is(400));
    }

    private String getTestAuthorizationQuery(String clientId, String redirect) throws Exception {
        return String.format("/oauth/authorize/?%s", JSONUtils.toFormUrlEncoded(
                new JSONParameter("client_id", clientId),
                new JSONParameter("redirect_uri", redirect),
                new JSONParameter("response_type", "code"))
        );
    }

    @WithMockUser
    @Test
    public void testSuccessfulAuthorizationRequest() throws Exception {
        String redirect = "https://test.com/auth";
        ITClientDTO clientDTO = this.mockITClientFactory
                .saveClient(this.mockITClientFactory
                        .generateClient(redirect));
        String query = getTestAuthorizationQuery(clientDTO.getClientId(), clientDTO.getWebServerRedirectUri());
        this.mockMvc.perform(get(query)).andExpect(redirectedUrlPattern(String.format("%s?code=**", redirect)));
    }

    @WithUserDetails("admin")
    @Test
    public void testSuccessfulAuthorizationCode() throws Exception {
        String redirect = "https://test.com/auth";
        ITClientDTO clientDTO = this.mockITClientFactory
                .saveClient(this.mockITClientFactory
                        .generateClient(redirect));
        String query = getTestAuthorizationQuery(clientDTO.getClientId(), clientDTO.getWebServerRedirectUri());
        MvcResult result = this.mockMvc.perform(get(query)).andReturn();
        String code = Objects.requireNonNull(result.getResponse().getRedirectedUrl()).split("code=")[1];
        String tokenQuery = JSONUtils.toFormUrlEncoded(

                new JSONParameter("client_id", clientDTO.getClientId()),
                new JSONParameter("client_secret", clientDTO.getClientSecret()),
                new JSONParameter("code", code),
                new JSONParameter("grant_type", "authorization_code"),
                new JSONParameter("redirect_uri", clientDTO.getWebServerRedirectUri())
        );
        this.mockMvc.perform(post("/oauth/token").content(tokenQuery)).andDo(print());
    }
}
