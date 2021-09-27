package it.chalmers.gamma.app.port.authentication;

import it.chalmers.gamma.app.domain.apikey.ApiKey;
import it.chalmers.gamma.app.domain.client.Client;

import java.util.Optional;

public interface ApiAuthenticated extends Authenticated {
    ApiKey get();

    /**
     * Api key might be connected to a client.
     */
    Optional<Client> getClient();
}