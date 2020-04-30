package it.chalmers.gamma.api;

import static it.chalmers.gamma.utils.ResponseUtils.expectedStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import it.chalmers.gamma.GammaApplication;
import it.chalmers.gamma.endoints.Endpoint;
import it.chalmers.gamma.endoints.EndpointsUtils;
import it.chalmers.gamma.endoints.Method;
import it.chalmers.gamma.factories.MockDatabaseGeneratorFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
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
@SuppressWarnings({"PMD.ExcessiveImports", "PMD.JUnitTestsShouldIncludeAssert"})
public class EndpointGETTests {

    private static final Logger LOGGER = LoggerFactory.getLogger(EndpointGETTests.class);


    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private MockDatabaseGeneratorFactory mockDatabaseGeneratorFactory;

    @Before
    public void setupTests() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        this.mockDatabaseGeneratorFactory.populateMockDatabase();

    }

    @WithUserDetails("admin")
    @Test
    public void testAllGETEndpointsAsAdmin() throws Exception {
        testGetEndpoints(
                Stream.of(
                        EndpointsUtils.getAuthorizedEndpoints(),
                        EndpointsUtils.getNonAuthorizedEndpoints(),
                        EndpointsUtils.getNormalUserEndpoints())
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList()), new ArrayList<>());
    }

    @WithMockUser(username = "normal")
    @Test       // TODO Generate and populate database with mock data
    public void testAllGETEndpointsAsNormalUser() throws Exception {
        testGetEndpoints(
                Stream.concat(
                        EndpointsUtils.getNormalUserEndpoints().stream(),
                        EndpointsUtils.getNonAuthorizedEndpoints().stream())
                        .collect(Collectors.toList()),
                EndpointsUtils.getAuthorizedEndpoints());
    }

    @WithAnonymousUser
    @Test
    public void testAllGETEndpointsAsAnonymous() throws Exception {
        testGetEndpoints(EndpointsUtils.getNonAuthorizedEndpoints(),
                Stream.concat(
                        EndpointsUtils.getNormalUserEndpoints().stream(),
                        EndpointsUtils.getAuthorizedEndpoints().stream())
                        .collect(Collectors.toList()));
    }

    private void testGetEndpoints(List<Endpoint> allowedEndpoints, List<Endpoint> deniedEndpoints) throws Exception {
        for (Endpoint endpoint : allowedEndpoints) {
            this.testGetEndpoint(String.format(endpoint.getPath(),
                    this.mockDatabaseGeneratorFactory.getMockedUUID(endpoint.getMockClass())),
                    endpoint.getMethod(), true);
        }
        for (Endpoint endpoint : deniedEndpoints) {
            this.testGetEndpoint(String.format(endpoint.getPath(),
                    this.mockDatabaseGeneratorFactory.getMockedUUID(endpoint.getMockClass())),
                    endpoint.getMethod(), false);
        }
    }

    private void testGetEndpoint(String endpoint, Method method, boolean authorized) throws Exception {
        if (method.equals(Method.GET)) {
            LOGGER.info(String.format("testing %s", endpoint));
            this.mockMvc.perform(get(endpoint, String.class)).andExpect(expectedStatus(authorized)).andDo(print());
        }

    }
}
