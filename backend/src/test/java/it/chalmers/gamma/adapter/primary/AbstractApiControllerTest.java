package it.chalmers.gamma.adapter.primary;

import io.restassured.RestAssured;
import io.restassured.filter.FilterContext;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import io.restassured.spi.AuthFilter;
import it.chalmers.gamma.GammaApplication;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(classes = GammaApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class AbstractApiControllerTest {

    @LocalServerPort
    protected int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        RestAssured.defaultParser = Parser.JSON;
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    protected AuthFilter apiAuthFilter(String apiKey) {
        return (requestSpec, responseSpec, context) -> {
            requestSpec.header("Authorization", "pre-shared " + apiKey);
            return context.next(requestSpec, responseSpec);
        };
    }

}
