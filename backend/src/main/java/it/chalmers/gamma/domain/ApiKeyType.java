package it.chalmers.gamma.domain;

import it.chalmers.gamma.api.ApiV1Controller;
import it.chalmers.gamma.api.ChalmersitApiController;
import it.chalmers.gamma.api.GoldappsApiController;

public enum ApiKeyType {
    CLIENT(ApiV1Controller.URI), GOLDAPPS(GoldappsApiController.URI), CHALMERSIT(ChalmersitApiController.API);

    ApiKeyType(String uri) {
        this.URI = uri;
    }

    public final String URI;
}
