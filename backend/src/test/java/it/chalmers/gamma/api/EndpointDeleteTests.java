package it.chalmers.gamma.api;

import static it.chalmers.gamma.utils.ResponseUtils.expectedStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import it.chalmers.gamma.GammaApplication;
import it.chalmers.gamma.endoints.Endpoint;
import it.chalmers.gamma.endoints.EndpointsUtils;
import it.chalmers.gamma.endoints.Method;
import it.chalmers.gamma.factories.MockDatabaseGeneratorFactory;
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
@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
public class EndpointDeleteTests {

    private static final Logger LOGGER = LoggerFactory.getLogger(EndpointDeleteTests.class);


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

    // This should probably not be performed, as some sanity checks should be done before deleting some entities
    /* @Test
    @WithUserDetails("admin")
    public void testDeleteAsAdminUser() throws Exception{
        testDeleteEndpoints(true);
    }*/

    @Test
    @WithMockUser
    public void testDeleteAsNormalUser() throws Exception {
        testDeleteEndpoints(false);
    }

    private void testDeleteEndpoints(boolean authorized) throws Exception {
        List<Endpoint> endpoints = Stream.of(
                EndpointsUtils.getAuthorizedEndpoints(),
                EndpointsUtils.getNonAuthorizedEndpoints(),
                EndpointsUtils.getNormalUserEndpoints())
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        for (Endpoint endpoint : endpoints) {
            this.testDeleteEndpoint(String.format(endpoint.getPath(),
                    this.mockDatabaseGeneratorFactory.getMockedUUID(endpoint.getMockClass())),
                    endpoint.getMethod(), authorized);
        }
    }

    private void testDeleteEndpoint(String endpoint, Method method, boolean authorized) throws Exception {
        if (method.equals(Method.DELETE)) {
            LOGGER.info(String.format("testing %s", endpoint));
            this.mockMvc.perform(delete(endpoint)).andDo(print()).andExpect(expectedStatus(authorized));
        }
    }

}
