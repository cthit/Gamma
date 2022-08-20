package it.chalmers.gamma.adapter.primary.external.info;

import it.chalmers.gamma.adapter.primary.AbstractApiControllerTest;
import it.chalmers.gamma.app.settings.domain.SettingsRepository;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupType;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static io.restassured.RestAssured.given;

@ActiveProfiles("test-with-mock")
class InfoApiControllerTest extends AbstractApiControllerTest {

    @Autowired
    private SettingsRepository settingsRepository;

    @Autowired
    private SuperGroupTypeRepository superGroupTypeRepository;

    @BeforeEach
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void test() {
        settingsRepository.setSettings(
                settings -> settings.withInfoSuperGroupTypes(
                        List.of(new SuperGroupType("committee"))
                ));

        given()
                .filter(apiAuthFilter("INFO-super-secret-code"))
            .and()
                .get("/api/external/info/groups");
    }

}