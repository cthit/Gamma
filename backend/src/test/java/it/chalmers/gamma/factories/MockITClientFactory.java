package it.chalmers.gamma.factories;

import it.chalmers.gamma.client.dto.ClientDTO;
import it.chalmers.gamma.client.service.ClientService;
import it.chalmers.gamma.utils.CharacterTypes;
import it.chalmers.gamma.utils.GenerationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MockITClientFactory {

    @Autowired
    private ClientService clientService;

    public ClientDTO generateClient(String redirect) {
        return new ClientDTO.ClientDTOBuilder().setId(redirect).setClientId(GenerationUtils.generateRandomString(20, CharacterTypes.LOWERCASE)).setClientSecret(GenerationUtils.generateText()).setWebServerRedirectUri(true).createClientDTO();
    }

    public ClientDTO generateClientNonAutoApprove(String redirect) {
        return new ClientDTO.ClientDTOBuilder().setId(redirect).setClientId(GenerationUtils.generateRandomString(20, CharacterTypes.LOWERCASE)).setClientSecret(GenerationUtils.generateText()).setWebServerRedirectUri(false).createClientDTO();

    }

    public ClientDTO saveClient(ClientDTO client) {
        return this.clientService.createITClient(
                client.getName(),
                client.getDescription(),
                client.getWebServerRedirectUri(),
                client.isAutoApprove());
    }
}
