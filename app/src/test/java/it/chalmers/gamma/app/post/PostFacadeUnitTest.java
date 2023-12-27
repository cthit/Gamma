package it.chalmers.gamma.app.post;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.security.core.context.SecurityContextHolder;

public class PostFacadeUnitTest {

    @BeforeEach
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

}