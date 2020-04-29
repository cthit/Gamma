package it.chalmers.gamma.api;

import it.chalmers.gamma.Endoints.Endpoint;
import it.chalmers.gamma.Endoints.Endpoints;
import it.chalmers.gamma.Endoints.Method;
import it.chalmers.gamma.GammaApplication;
import it.chalmers.gamma.factories.MockDatabaseGeneratorFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import static it.chalmers.gamma.utils.ResponseUtils.expectedStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
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
public class EndpointGETTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private MockDatabaseGeneratorFactory mockDatabaseGeneratorFactory;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        this.mockDatabaseGeneratorFactory.generateNewMock();

    }

    @WithMockUser(username = "admin", authorities = "admin")
    @Test
    public void testAllGETEndpointsAsAdmin() throws Exception {
        testGetEndpoints(
                Stream.of(
                        Endpoints.getAuthorizedEndpoints(),
                        Endpoints.getNonAuthorizedEndpoints(),
                        Endpoints.getNormalUserEndpoints())
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList()), new ArrayList<>());
    }

    @WithMockUser(username = "normal")
    @Test       // TODO Generate and populate database with mock data
    public void testAllGETEndpointsAsNormalUser() throws Exception {
        testGetEndpoints(
                Stream.concat(
                        Endpoints.getNormalUserEndpoints().stream(),
                        Endpoints.getNonAuthorizedEndpoints().stream())
                        .collect(Collectors.toList()),
                Endpoints.getAuthorizedEndpoints());
    }

    @WithAnonymousUser
    @Test
    public void testAllGETEndpointsAsAnonymous() throws Exception {
        testGetEndpoints(Endpoints.getNonAuthorizedEndpoints(),
                Stream.concat(
                        Endpoints.getNormalUserEndpoints().stream(),
                        Endpoints.getAuthorizedEndpoints().stream())
                        .collect(Collectors.toList()));
    }

    private void testGetEndpoints(List<Endpoint> allowedEndpoints, List<Endpoint> nonAllowedEndpoints) throws Exception {
        for (Endpoint endpoint : allowedEndpoints) {
            this.testGetEndpoint(String.format(endpoint.getPath(),
                    mockDatabaseGeneratorFactory.getMockedUUID(endpoint.getMockClass())),
                    endpoint.getMethod(), true);
        }
        for (Endpoint endpoint : nonAllowedEndpoints) {
            this.testGetEndpoint(String.format(endpoint.getPath(),
                    mockDatabaseGeneratorFactory.getMockedUUID(endpoint.getMockClass())),
                    endpoint.getMethod(), false);
        }
    }

    private void testGetEndpoint(String endpoint, Method method, boolean authorized) throws Exception {
        System.out.println(endpoint + " " + method);
        if (method.equals(Method.GET)) {
            this.mockMvc.perform(get(endpoint, String.class)).andExpect(expectedStatus(authorized));
        }

    }
}
