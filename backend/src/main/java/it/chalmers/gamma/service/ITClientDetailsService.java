package it.chalmers.gamma.service;

import it.chalmers.gamma.db.repository.ITClientDetailsRepository;

import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;

public class ITClientDetailsService implements ClientDetailsService {

    private ITClientDetailsRepository itClientDetailsRepository;

    public ITClientDetailsService(ITClientDetailsRepository itClientDetailsRepository){
        this.itClientDetailsRepository = itClientDetailsRepository;
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        return this.itClientDetailsRepository.findByClientId(clientId);
    }
}
