package it.chalmers.gamma.adapter.primary.api.accountscaffold;

import it.chalmers.gamma.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class AccountScaffoldV1ApiControllerTest {

  @Test
  public void test() {}
}
