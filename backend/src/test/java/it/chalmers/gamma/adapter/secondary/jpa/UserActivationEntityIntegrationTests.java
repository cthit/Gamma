package it.chalmers.gamma.adapter.secondary.jpa;

import it.chalmers.gamma.adapter.secondary.jpa.user.UserActivationRepositoryAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@ActiveProfiles("test")
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({UserActivationRepositoryAdapter.class})
public class UserActivationEntityIntegrationTests {

    @Test
    void Given_ValidUser_Expect_save_To_Work() {

    }
}
