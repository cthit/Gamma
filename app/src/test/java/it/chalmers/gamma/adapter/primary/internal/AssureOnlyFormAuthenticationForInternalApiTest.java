package it.chalmers.gamma.adapter.primary.internal;

import io.restassured.authentication.FormAuthConfig;
import io.restassured.filter.session.SessionFilter;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.*;

@ActiveProfiles("test-with-mock")
public class AssureOnlyFormAuthenticationForInternalApiTest extends AbstractInternalApiControllerTest{

    @Test
    public void basicAuthenticationShouldFail() {
        given()
                .auth().basic("admin", "password")
                .get("/internal/users/me")
                .then()
                .statusCode(401);
    }

    @Test
    public void digestAuthenticationShouldFail() {
        given()
                .auth().digest("admin", "password")
                .get("/internal/users/me")
                .then()
                .statusCode(401);
    }

    @Test
    public void formAuthenticationShouldSucceed() {
        givenAdminUser()
                .get("/internal/users/me").then().statusCode(200);
    }


}
