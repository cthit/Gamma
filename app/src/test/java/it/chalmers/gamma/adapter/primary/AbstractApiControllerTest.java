package it.chalmers.gamma.adapter.primary;

import io.restassured.RestAssured;
import io.restassured.config.CsrfConfig;
import io.restassured.config.SessionConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.parsing.Parser;
import it.chalmers.gamma.GammaApplication;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
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
        RestAssured.config = RestAssured.config()
                .sessionConfig(new SessionConfig()
                        .sessionIdName("gamma")
                )
                .csrfConfig(new CsrfConfig("/login")
                        .csrfHeaderName("X-XSRF-TOKEN")
                );
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

}
