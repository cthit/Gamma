package it.chalmers.gamma.adapter.primary.api;

import it.chalmers.gamma.app.apikey.domain.ApiKeyRepository;
import it.chalmers.gamma.app.settings.domain.SettingsRepository;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test-with-mock")
class InfoV1ApiControllerTest extends AbstractExternalApiControllerTest {

    @Autowired
    private SettingsRepository settingsRepository;

    @Autowired
    private ApiKeyRepository apiKeyRepository;


    private static final String expected = """
    {
      "groups": [
        {
          "id": "2abe2264-fd61-4899-ba46-851279d85229",
          "version": 1,
          "name": "digit2023",
          "prettyName": "digIT2023",
          "groupMembers": [],
          "superGroup": {
            "id": "aed27030-ad90-4526-855c-1e909b1dcecb",
            "version": 1,
            "name": "digit",
            "prettyName": "digIT",
            "type": "committee",
            "svDescription": "",
            "enDescription": ""
          }
        },
        {
          "id": "1ed91274-13c8-4d6d-ab75-37c9d732b51b",
          "version": 1,
          "name": "prit2023",
          "prettyName": "P.R.I.T.2023",
          "groupMembers": [],
          "superGroup": {
            "id": "b3bcbbcc-0b93-4c41-a3c7-1792448c6fc1",
            "version": 1,
            "name": "prit",
            "prettyName": "P.R.I.T.",
            "type": "committee",
            "svDescription": "",
            "enDescription": ""
          }
        }
      ]
    }
    """;

    @Test
    public void groups() {
        System.out.println(apiKeyRepository.getAll());

        settingsRepository.setSettings(
                settings -> settings.withInfoSuperGroupTypes(
                        List.of(new SuperGroupType("committee"))
                ));

        var response = given()
                .filter(apiAuthFilter("INFO-super-secret-code"))
                .and()
                .get("/external/info/v1/groups")
                .andReturn();

        assertThat(response.print()).isEqualTo(expected.replaceAll("\\s+",""));

        given()
                .filter(apiAuthFilter("bad-key"))
                .and()
                .get("/external/info/v1/groups")
                .then()
                .statusCode(401);

        given()
                .filter(apiAuthFilter("ALLOW-LIST-super-secret-code"))
                .and()
                .get("/external/info/v1/groups")
                .then()
                .statusCode(403);
    }

}