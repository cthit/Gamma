package it.chalmers.gamma.app.domain.apikey;

import it.chalmers.gamma.adapter.primary.api.v1.ClientApiV1Controller;
import it.chalmers.gamma.adapter.primary.api.chalmersit.ChalmersitApiController;
import it.chalmers.gamma.adapter.primary.api.goldapps.GoldappsApiController;

public enum ApiKeyType {
    CLIENT(ClientApiV1Controller.URI), GOLDAPPS(GoldappsApiController.URI), CHALMERSIT(ChalmersitApiController.API);

    ApiKeyType(String uri) {
        this.URI = uri;
    }

    public final String URI;
}
