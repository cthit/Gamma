package it.chalmers.gamma.api;

import it.chalmers.gamma.GammaApplication;
import it.chalmers.gamma.config.WebSecurityConfig;
import it.chalmers.gamma.domain.dto.user.ITUserDTO;
import it.chalmers.gamma.factories.RandomITUserFactory;
import it.chalmers.gamma.filter.AuthenticationFilterConfigurer;
import it.chalmers.gamma.service.ITUserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = GammaApplication.class)
@ActiveProfiles("test")
public class LoginIntegrationTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private WebSecurityConfig springSecurityFilterChain;

    @Autowired
    private ITUserService userService;

    @Before
    public void setup()
    {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

    }


    @WithMockUser(username = "admin")
    @Test
    public void returnLoginWhenNotAuthenticated() throws Exception {
        assertNotNull(userService);
        System.out.println("got here");
    //    System.out.println(this.restTemplate.getForObject("/api/login", String.class));
        mockMvc.perform(get("/users/minified", String.class)).andDo(print());
    }

    public void testLogin() {
        ITUserDTO user = RandomITUserFactory.generateITUser();
    //    this.userService.createUser(user.getNick(), user.getFirstName(), user.getLastName(), user.getCid(),
     //           user.getAcceptanceYear(), user.isUserAgreement(), user.getEmail(),
    //            GenerationUtils.generateRandomString());
   //     this.mockMvc.perform();
    }


}
