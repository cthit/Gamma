package it.chalmers.gamma.adapter.primary.external;

import io.restassured.spi.AuthFilter;
import it.chalmers.gamma.adapter.primary.AbstractApiControllerTest;

public class AbstractExternalApiControllerTest extends AbstractApiControllerTest {

    protected AuthFilter apiAuthFilter(String apiKey) {
        return (requestSpec, responseSpec, context) -> {
            requestSpec.header("Authorization", "pre-shared " + apiKey);
            return context.next(requestSpec, responseSpec);
        };
    }

}
