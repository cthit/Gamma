package it.chalmers.gamma.api;

import it.chalmers.gamma.GammaApplication;
import it.chalmers.gamma.factories.RandomITUserFactory;

import it.chalmers.gamma.utils.JSONUtils;
import it.chalmers.gamma.utils.ResponseUtils;
import java.util.Objects;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = GammaApplication.class)
@ActiveProfiles("test")
public class AdminITUserTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @WithUserDetails("admin")
    @Test
    public void testAdminCreateUserAsAdmin() throws Exception{
        testAdminCreateUser(true);
    }

    @WithMockUser("nonAdmin")
    @Test
    public void testAdminCreateUserAsNonAdmin() throws Exception{
        testAdminCreateUser(false);
    }

    private void testAdminCreateUser(boolean authorized) throws Exception{
        this.mockMvc.perform(MockMvcRequestBuilders.post(
                "/admin/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(JSONUtils.objectToJSONString(
                        RandomITUserFactory.generateValidAdminCreateUserRequest()))))
                .andExpect(ResponseUtils.expectedStatus(authorized));
    }

    @WithUserDetails("admin")
    private void testAdminGetAllUsers() throws Exception{
        
    }

}
