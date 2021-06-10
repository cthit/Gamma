package it.chalmers.gamma.internal.client.service;

import it.chalmers.gamma.domain.AuthorityLevelName;
import it.chalmers.gamma.domain.Client;
import it.chalmers.gamma.domain.ClientId;
import it.chalmers.gamma.domain.ClientSecret;
import it.chalmers.gamma.domain.ApiKey;
import it.chalmers.gamma.domain.ApiKeyId;
import it.chalmers.gamma.domain.ClientWithRestrictions;
import it.chalmers.gamma.internal.apikey.service.ApiKeyService;
import it.chalmers.gamma.domain.ApiKeyToken;
import it.chalmers.gamma.domain.ApiKeyType;
import it.chalmers.gamma.domain.ClientApiKey;
import it.chalmers.gamma.internal.clientapikey.service.ClientApiKeyService;
import it.chalmers.gamma.internal.client.controller.ClientAdminController;

import it.chalmers.gamma.internal.clientrestriction.service.ClientRestrictionService;
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
        return this.clientRepository.findById(new ClientId(clientId))
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

        ApiKeyId apiKeyId = new ApiKeyId();

        this.apiKeyService.create(
                new ApiKey(
                        apiKeyId,
                        newClient.name(),
                        newClient.description(),
                        ApiKeyType.CLIENT
                ),
                apiKeyToken
        );

        this.clientApiKeyService.create(
                new ClientApiKey(
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

    public static class ClientNotFoundException extends Exception { }


}
