package it.chalmers.gamma.adapter.primary.internal;

import it.chalmers.gamma.app.settings.domain.SettingsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import static io.restassured.RestAssured.given;

@ActiveProfiles("test-with-mock")
public class InfoApiSettingsAdminControllerTest extends AbstractInternalApiControllerTest {

    @Autowired
    private SettingsRepository settingsRepository;

    @Test
    public void update() {
//        var currentLastUpdatedUserAgreement = settingsRepository.getSettings().lastUpdatedUserAgreement();

        record Request(List<String> superGroupTypes) { }

        var request = given()
                .cookie("haha", "haha")
                .header("omg", "omg")
//                .filter(adminAuthFilter())
                .and()
//                .body(new Request(List.of("committee", "board")))
                .get("/internal/admin/info-api-settings/super-group-types");

        System.out.println("headers");
        System.out.println(request.getHeaders());

        var response = request.thenReturn();

        System.out.println("HELLO");
        System.out.println(response.getStatusCode());
        System.out.println(response.getBody().prettyPrint());

//        var toCheckLastUpdatedUserAgreement = settingsRepository.getSettings().lastUpdatedUserAgreement();
//
//        assertThat(currentLastUpdatedUserAgreement)
//                .isEqualTo(toCheckLastUpdatedUserAgreement);
    }

}
