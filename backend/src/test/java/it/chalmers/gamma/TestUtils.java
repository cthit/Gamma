package it.chalmers.gamma;

import it.chalmers.gamma.service.ITUserService;

import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

@Component
@SuppressWarnings("PMD.AvoidPrintStackTrace")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class TestUtils {

    private MockMvc mockMvc;

    private ITUserService userService;

    public void setMockMvc(MockMvc mockMvc, ITUserService userService) {
        this.mockMvc = mockMvc;
        this.userService = userService;
    }

    public MockMvc getMockMvc() {
        return this.mockMvc;
    }

    public ITUserService getUserService() {
        return this.userService;
    }

}
