package it.chalmers.gamma.api;

import it.chalmers.gamma.Endoints.Endpoint;
import it.chalmers.gamma.Endoints.Endpoints;
import it.chalmers.gamma.Endoints.Method;
import it.chalmers.gamma.GammaApplication;
import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.domain.dto.user.ITUserDTO;
import it.chalmers.gamma.factories.RandomITUserFactory;
import it.chalmers.gamma.service.ITUserService;
import it.chalmers.gamma.util.TokenUtils;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = GammaApplication.class)
@ActiveProfiles("test")
public class AuthorizationTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private ITUserService userService;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

    }

    @WithMockUser(username = "admin")
    @Test
    public void testAllEndpointsAsAdmin() throws Exception {
        System.out.println("got here");
        mockMvc.perform(get("/users/minified", String.class)).andDo(print());
    }

    @WithMockUser(username = "normal")
    @Test       // TODO Generate and populate database with mock data
    public void testAllEndpointsAsNormalUser() throws Exception {
        ITUserDTO user = RandomITUserFactory.generateITUser("normal");
        user = userService.createUser(user.getNick(), user.getFirstName(), user.getLastName(), user.getCid(),
                user.getAcceptanceYear(), user.isUserAgreement(), user.getEmail(), TokenUtils.generateToken());
        System.out.println(userService.loadAllUsers());
       List<Endpoint> normalUserEndpoints = Endpoints.getNormalUserEndpoints();
       for (Endpoint endpoint : normalUserEndpoints) {
           testEndpoint(String.format(endpoint.getPath(), user.getId()), endpoint.getMethod(), true);
       }
    }

    @WithAnonymousUser
    @Test
    public void testAllEndpointsAsAnonymous() throws Exception {

    }

    private ResultMatcher expectedStatus(boolean authorized) {
        if (authorized) {
            return status().is2xxSuccessful();
        }
        else return status().is4xxClientError();
    }

    private void testEndpoint(String endpoint, Method method, boolean authorized) throws Exception {
        System.out.println(endpoint);
            switch (method) {
                case GET:
                    this.mockMvc.perform(get(endpoint, String.class)).andExpect(expectedStatus(authorized));
                    break;
                case PUT:
                    this.mockMvc.perform(put(endpoint, String.class)).andExpect(expectedStatus(authorized));
                    break;
                case POST:
                    this.mockMvc.perform(post(endpoint, String.class)).andExpect(expectedStatus(authorized));
                    break;
                case DELETE:
                    this.mockMvc.perform(delete(endpoint, String.class)).andExpect(expectedStatus(authorized));
                    break;
                default:
                    break;
            }
    }
}
