package it.chalmers.gamma.domain.apikey;

import it.chalmers.gamma.adapter.primary.api.ApiV1Controller;
import it.chalmers.gamma.adapter.primary.api.ChalmersitApiController;
import it.chalmers.gamma.adapter.primary.api.GoldappsApiController;

public enum ApiKeyType {
    CLIENT(ApiV1Controller.URI), GOLDAPPS(GoldappsApiController.URI), CHALMERSIT(ChalmersitApiController.API);

    ApiKeyType(String uri) {
        this.URI = uri;
    }

    public final String URI;
}
