package it.chalmers.gamma.app.client.service;

import it.chalmers.gamma.app.domain.AuthorityLevelName;
import it.chalmers.gamma.app.domain.Client;
import it.chalmers.gamma.app.domain.ClientId;
import it.chalmers.gamma.app.domain.ClientSecret;
import it.chalmers.gamma.app.domain.ApiKey;
import it.chalmers.gamma.app.domain.ApiKeyId;
import it.chalmers.gamma.app.domain.ClientWithRestrictions;
import it.chalmers.gamma.app.apikey.service.ApiKeyService;
import it.chalmers.gamma.app.domain.ApiKeyToken;
import it.chalmers.gamma.app.domain.ApiKeyType;
import it.chalmers.gamma.app.domain.ClientApiKeyPair;
import it.chalmers.gamma.app.service.ClientApiKeyService;
import it.chalmers.gamma.app.client.controller.ClientAdminController;

import it.chalmers.gamma.app.service.ClientRestrictionService;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientService implements ClientDetailsService {

    private final ClientRepository clientRepository;
    private final ApiKeyService apiKeyService;
    private final ClientApiKeyService clientApiKeyService;
    private final ClientRestrictionService clientRestrictionService;

    public ClientService(ClientRepository clientRepository,
                         ApiKeyService apiKeyService,
                         ClientApiKeyService clientApiKeyService,
                         ClientRestrictionService clientRestrictionService) {
        this.clientRepository = clientRepository;
        this.apiKeyService = apiKeyService;
        this.clientApiKeyService = clientApiKeyService;
        this.clientRestrictionService = clientRestrictionService;
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) {
        return this.clientRepository.findById(ClientId.valueOf(clientId))
                .map(clientEntity -> new ClientDetailsImpl(clientEntity.toDTO(), clientEntity.getClientSecret()))
                .orElseThrow(ClientAdminController.ClientNotFoundResponse::new);
    }

    @Transactional
    public void create(Client newClient, ClientSecret clientSecret, List<AuthorityLevelName> restrictions) {
        this.clientRepository.save(new ClientEntity(newClient, clientSecret));
        this.clientRestrictionService.create(newClient.clientId(), restrictions);
    }

    @Transactional
    public void createWithApiKey(Client newClient,
                                 ClientSecret clientSecret,
                                 ApiKeyToken apiKeyToken,
                                 List<AuthorityLevelName> restrictions) {
        this.create(newClient, clientSecret, restrictions);

        ApiKeyId apiKeyId = ApiKeyId.generate();

        this.apiKeyService.create(
                new ApiKey(
                        apiKeyId,
                        newClient.prettyName(),
                        newClient.description(),
                        ApiKeyType.CLIENT
                ),
                apiKeyToken
        );

        this.clientApiKeyService.create(
                new ClientApiKeyPair(
                        newClient.clientId(),
                        apiKeyId
                )
        );
    }

    public void delete(ClientId clientId) throws ClientNotFoundException {
        this.clientRepository.deleteById(clientId);
    }

    public List<Client> getAll() {
        return this.clientRepository.findAll().stream().map(ClientEntity::toDTO).collect(Collectors.toList());
    }

    public ClientWithRestrictions get(ClientId clientId) throws ClientNotFoundException {
        return new ClientWithRestrictions(
                this.clientRepository.findById(clientId)
                        .orElseThrow(ClientNotFoundException::new)
                        .toDTO(),
                this.clientRestrictionService.get(clientId)
                );
    }

    public void resetClientSecret(ClientId clientId, ClientSecret clientSecret) throws ClientNotFoundException {
        ClientEntity clientEntity = this.clientRepository.findById(clientId)
                .orElseThrow(ClientNotFoundException::new);
        clientEntity.setClientSecret(clientSecret);
        this.clientRepository.save(clientEntity);
    }

    public static class ClientNotFoundException extends Exception { }


}
