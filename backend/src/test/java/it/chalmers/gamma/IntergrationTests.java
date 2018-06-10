package it.chalmers.gamma;

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
public class IntergrationTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ITUserService userService;

    @Test
    public void contextLoads() throws Exception {       // This is an ugly way to do this, it should probably be fixed
        userService.createUser("gurr", "engsmyre");
        MvcResult result =  mockMvc.perform
                (MockMvcRequestBuilders.get("/users/")
                        .accept(MediaType.APPLICATION_JSON)).andReturn();
        Assert.assertTrue(result.getResponse().getContentAsString().contains("gurr"));
    }
}
