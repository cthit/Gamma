package it.chalmers.gamma.adapter.primary.internal;

import io.restassured.authentication.FormAuthConfig;
import io.restassured.filter.session.SessionFilter;
import io.restassured.specification.RequestSpecification;
import it.chalmers.gamma.adapter.primary.AbstractApiControllerTest;

import static io.restassured.RestAssured.given;


public class AbstractInternalApiControllerTest extends AbstractApiControllerTest {

    protected RequestSpecification givenAdminUser() {
        return givenAdminUser(null);
    }

    protected RequestSpecification givenAdminUser(SessionFilter sessionFilter) {
        FormAuthConfig formAuthConfig = new FormAuthConfig("/login", "username", "password");
        var request = given()
                .auth().form("admin", "password", formAuthConfig);

        if(sessionFilter != null) {
            request.filter(sessionFilter);
        }

        return request;
    }

    protected RequestSpecification givenSignedInUser(SessionFilter sessionFilter) {
        return given().filter(sessionFilter);
    }

}
