package it.chalmers.gamma.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.chalmers.gamma.GammaApplication;
import it.chalmers.gamma.user.dto.UserDTO;
import it.chalmers.gamma.endoints.JSONParameter;
import it.chalmers.gamma.factories.MockITUserFactory;
import it.chalmers.gamma.utils.CharacterTypes;
import it.chalmers.gamma.utils.GenerationUtils;
import it.chalmers.gamma.utils.JSONUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
public class LoginTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private MockITUserFactory mockITUserFactory;

    @Value("${application.frontend-client-details.successful-login-uri}")
    private String frontendUri;

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    @Before
    public void setupTests() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    public void testSuccessfulLogin() throws Exception {
        UserDTO user = this.mockITUserFactory.saveUser(
                this.mockITUserFactory.generateITUser(
                        GenerationUtils.generateRandomString(
                                10,
                                CharacterTypes.LOWERCASE),
                        true));
        String request = JSONUtils.toFormUrlEncoded(
                new JSONParameter(USERNAME, user.getCid()),
                new JSONParameter(PASSWORD, PASSWORD)
        );
        testLogin(request, this.frontendUri);
    }

    @Test
    public void testWrongPasswordLogin() throws Exception {
        String invalidPasswordRequest = JSONUtils.toFormUrlEncoded(
                new JSONParameter(USERNAME, "user"),
                new JSONParameter(PASSWORD, "invalidPassword")
        );
        testLogin(invalidPasswordRequest, "/login?error");
    }

    @Test
    public void testWrongUsernameLogin() throws Exception {
        String invalidUsernameRequest = JSONUtils.toFormUrlEncoded(
                new JSONParameter(USERNAME, "invalidUser"),
                new JSONParameter(PASSWORD, PASSWORD)
        );
        testLogin(invalidUsernameRequest, "/login?error");
    }

    @Test
    public void testNonActivatedRedirect() throws Exception {
        UserDTO user = this.mockITUserFactory.generateITUser("nActUser", false);
        UserDTO editedUser = this.mockITUserFactory.saveUser(user);
        String nonActivatedPasswordRequest = JSONUtils.toFormUrlEncoded(
                new JSONParameter(USERNAME, editedUser.getCid()),
                new JSONParameter(PASSWORD, PASSWORD) // Does not do any difference
        );
        testLogin(nonActivatedPasswordRequest,
                String.format("%s/reset-password/finish?accountLocked=true", this.frontendUri));
    }

    @Test
    public void testSuccessfulEmailLogin() throws Exception {
        UserDTO user = this.mockITUserFactory.saveUser(
                this.mockITUserFactory.generateITUser(
                        GenerationUtils.generateRandomString(
                                10,
                                CharacterTypes.LOWERCASE),
                        true));
        String request = JSONUtils.toFormUrlEncoded(
                new JSONParameter(USERNAME, user.getEmail()),
                new JSONParameter(PASSWORD, PASSWORD)
        );
        testLogin(request, this.frontendUri);
    }


    private void testLogin(String request, String redirect) throws Exception {
        this.mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(
                        request
                ))
                .andExpect(redirectedUrl(redirect))
                .andExpect(status().is(302));
    }

}
