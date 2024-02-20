package it.chalmers.gamma.adapter.primary.oauth2;

import static io.restassured.RestAssured.given;

import it.chalmers.gamma.adapter.primary.AbstractApiControllerTest;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test-with-mock")
public class OAuth2CodeFlowTest extends AbstractApiControllerTest {

  @Test
  public void testHappyAuthorizationCodeFlow() {
    // http://gamma:8081/oauth2/authorize?response_type=code&client_id=test&scope=openid
    // profile&state=_RRya178Aii4_JcPADFCA7iBe5e3ihPMdyDD9S0V_t8=&redirect_uri=http://client:3001/login/oauth2/code/gamma&nonce=e-D773_CxPQy0-OQWeNMbQlMKF_5PXjg21n0EddTsTU

    String state = "mystate";
    String nonce = "mynonce";

    Map<String, String> parameters =
        Map.of(
            "clientId",
            "test",
            "scope",
            "profile",
            "state",
            state,
            "redirect_uri",
            "http://client:3001/login/oauth2/code/gamma",
            "nonce",
            nonce);

    var response =
        given()
            .get(
                "/oauth2/authorize?response_type=code&client_id={clientId}&scope={scope}&state={state}&redirect_uri={redirect_uri}&nonce={nonce}",
                parameters)
            .thenReturn();

    System.out.println(response.getBody().prettyPrint());
  }
}
