package it.chalmers.gamma.factories;

import it.chalmers.gamma.client.ITClientDTO;
import it.chalmers.gamma.client.ITClientService;
import it.chalmers.gamma.utils.CharacterTypes;
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
                GenerationUtils.generateRandomString(20, CharacterTypes.LOWERCASE),
                GenerationUtils.generateText(),
                true
        );
    }

    public ITClientDTO generateClientNonAutoApprove(String redirect) {
        return new ITClientDTO(
                redirect,
                GenerationUtils.generateRandomString(20, CharacterTypes.LOWERCASE),
                GenerationUtils.generateText(),
                false
        );

    }

    public ITClientDTO saveClient(ITClientDTO client) {
        return this.clientService.createITClient(
                client.getName(),
                client.getDescription(),
                client.getWebServerRedirectUri(),
                client.isAutoApprove());
    }
}
