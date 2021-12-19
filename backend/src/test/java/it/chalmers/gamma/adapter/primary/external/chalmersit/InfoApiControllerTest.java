package it.chalmers.gamma.adapter.primary.external.chalmersit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InfoApiControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void setUp() {
    }

    @Test
    void getGroups() {
        // given
//        given(superHeroRepository.getSuperHero(2))
//                .willReturn(new SuperHero("Rob", "Mannon", "RobotMan"));

        record ExpectedGroup(String name) {

        }

        record ExpectedResponse(List<ExpectedGroup> groups) {

        }

        // when
        ResponseEntity<ExpectedResponse> response = restTemplate
                .getForEntity(
                        "/api/external/chalmersit/groups",
                        ExpectedResponse.class
                );

        System.out.println(response);
    }

}