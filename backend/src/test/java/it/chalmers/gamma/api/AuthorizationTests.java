package it.chalmers.gamma.api;

import it.chalmers.gamma.Endoints.Endpoint;
import it.chalmers.gamma.Endoints.Endpoints;
import it.chalmers.gamma.Endoints.Method;
import it.chalmers.gamma.GammaApplication;
import it.chalmers.gamma.domain.dto.user.WhitelistDTO;
import it.chalmers.gamma.factories.MockDatabaseGeneratorFactory;
import it.chalmers.gamma.factories.RandomITUserFactory;
import it.chalmers.gamma.utils.JSONUtils;
import java.util.List;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import static it.chalmers.gamma.utils.ResponseUtils.expectedStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = GammaApplication.class)
@ActiveProfiles("test")
public class AuthorizationTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    MockDatabaseGeneratorFactory mockDatabaseGeneratorFactory;

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
        mockDatabaseGeneratorFactory.generateNewMock();
       List<Endpoint> normalUserEndpoints = Endpoints.getNormalUserEndpoints();
       for (Endpoint endpoint : normalUserEndpoints) {
           testEndpoint(String.format(endpoint.getPath(), mockDatabaseGeneratorFactory.getMockedUUID(endpoint.getC())),
                   endpoint.getMethod(), true);
       }
    }

    @WithAnonymousUser
    @Test
    public void testAllEndpointsAsAnonymous() throws Exception {

    }

    private void testEndpoint(String endpoint, Method method, boolean authorized) throws Exception {
        System.out.println(endpoint + method.name());
            switch (method) {
                case GET:
                    this.mockMvc.perform(get(endpoint, String.class)).andExpect(expectedStatus(authorized));
                    break;
                case PUT:
                    this.mockMvc.perform(put(endpoint, String.class)).andExpect(expectedStatus(authorized));
                    break;
                case POST:
                    this.mockMvc.perform(post(endpoint, String.class).contentType(MediaType.APPLICATION_JSON)
                            .content(JSONUtils.objectToJSONString(
                                    RandomITUserFactory.generateValidAdminCreateUserRequest())))
                            .andExpect(expectedStatus(authorized));
                    break;
                case DELETE:
                    this.mockMvc.perform(delete(endpoint, String.class)).andExpect(expectedStatus(authorized));
                    break;
                default:
                    break;
            }
    }
}
