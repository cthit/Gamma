package it.chalmers.gamma.app.authentication;

import it.chalmers.gamma.app.apikey.domain.ApiKey;
import it.chalmers.gamma.app.client.domain.Client;

import java.util.Optional;

public non-sealed interface ApiAuthenticated extends Authenticated {
    ApiKey get();

    /**
     * Api key might be connected to a client.
     */
    Optional<Client> getClient();
}
