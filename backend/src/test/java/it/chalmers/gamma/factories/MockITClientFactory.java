package it.chalmers.gamma.factories;

import it.chalmers.gamma.domain.dto.access.ITClientDTO;
import it.chalmers.gamma.service.ITClientService;
import it.chalmers.gamma.utils.GenerationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MockITClientFactory {

    @Autowired
    private ITClientService clientService;

    public ITClientDTO generateClient(String redirect) {
        return new ITClientDTO(
            redirect,
                GenerationUtils.generateRandomString(20, GenerationUtils.CharacterTypes.LOWERCASE),
                GenerationUtils.generateText()
        );
    }

    public ITClientDTO saveClient(ITClientDTO client) {
        return this.clientService.createITClient(
                client.getName(),
                client.getDescription(),
                client.getWebServerRedirectUri());
    }
}
