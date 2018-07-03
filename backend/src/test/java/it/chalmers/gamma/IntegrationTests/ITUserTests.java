package it.chalmers.gamma.IntegrationTests;


import it.chalmers.gamma.service.ITUserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
        //      classes = GammaApplication.class
)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class ITUserTests {

    @Autowired
    ITUserService userService;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void testCreateUser() throws Exception {
        System.out.println(userService.createUser("gurr", "engsmyre"));
        System.out.println(userService.createUser("leif", "test"));

        MvcResult result =  mockMvc.perform
                (MockMvcRequestBuilders.get("/users/")
                        .accept(MediaType.APPLICATION_JSON)).andReturn();
        System.out.println(result.getResponse().getContentAsString());
        Assert.assertTrue(result.getResponse().getContentAsString().contains("gurr"));
        Assert.assertFalse(result.getResponse().getContentAsString().contains("sfe"));
    }
}
