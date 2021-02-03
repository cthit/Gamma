package it.chalmers.gamma;

import it.chalmers.gamma.user.service.UserService;

import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

@Component
@SuppressWarnings("PMD.AvoidPrintStackTrace")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class TestUtils {

    private MockMvc mockMvc;

    private UserService userService;

    public void setMockMvc(MockMvc mockMvc, UserService userService) {
        this.mockMvc = mockMvc;
        this.userService = userService;
    }

    public MockMvc getMockMvc() {
        return this.mockMvc;
    }

    public UserService getUserService() {
        return this.userService;
    }

}
