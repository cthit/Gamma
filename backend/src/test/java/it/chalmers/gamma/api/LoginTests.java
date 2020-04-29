package it.chalmers.gamma.api;

import it.chalmers.gamma.Endoints.JSONParameter;
import it.chalmers.gamma.GammaApplication;
import it.chalmers.gamma.domain.dto.user.ITUserDTO;
import it.chalmers.gamma.factories.MockDatabaseGeneratorFactory;
import it.chalmers.gamma.factories.MockITUserFactory;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = GammaApplication.class)
@ActiveProfiles("test")
public class LoginTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private MockDatabaseGeneratorFactory mockDatabaseGeneratorFactory;

     @Autowired
     private MockITUserFactory mockITUserFactory;

    @Value("${application.frontend-client-details.successful-login-uri}")
    private String frontendUri;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        this.mockDatabaseGeneratorFactory.generateNewMock();
    }

    @Test
    public void testSuccessfulLogin() throws Exception {
        ITUserDTO userDTO = this.mockDatabaseGeneratorFactory.getMockedUser();
        String request = JSONUtils.toFormUrlEncoded(
                new JSONParameter("username", userDTO.getCid()),
                new JSONParameter("password", "password")
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

    @Test
    public void testWrongPasswordLogin() throws Exception {
        String invalidPasswordRequest = JSONUtils.toFormUrlEncoded(
                new JSONParameter("username", "user"),
                new JSONParameter("password", "invalidPassword")
        );
        testLogin(invalidPasswordRequest, "/login?error");
    }

    @Test
    public void testWrongUsernameLogin() throws Exception {
        String invalidUsernameRequest = JSONUtils.toFormUrlEncoded(
                new JSONParameter("username", "invalidUser"),
                new JSONParameter("password", "password")
        );
        testLogin(invalidUsernameRequest, "/login?error");
    }

    @Test
    public void testNonActivatedRedirect() throws Exception {
        ITUserDTO user = this.mockITUserFactory.generateITUser("nActUser", false);
        ITUserDTO editedUser = this.mockITUserFactory.saveUser(user);
        String nonActivatedPasswordRequest = JSONUtils.toFormUrlEncoded(
                new JSONParameter("username", editedUser.getCid()),
                new JSONParameter("password", "password") // Does not do any difference
        );
        testLogin(nonActivatedPasswordRequest, String.format("%s/reset-password/finish?accountLocked=true", this.frontendUri));
    }
}
