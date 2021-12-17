package it.chalmers.gamma.app.domain.apikey;

import it.chalmers.gamma.adapter.primary.external.v1.ClientApiV1Controller;
import it.chalmers.gamma.adapter.primary.external.chalmersit.ChalmersitApiController;
import it.chalmers.gamma.adapter.primary.external.goldapps.GoldappsApiController;
import it.chalmers.gamma.adapter.primary.external.whitelist.WhitelistApiController;

public enum ApiKeyType {
    CLIENT(ClientApiV1Controller.URI),
    GOLDAPPS(GoldappsApiController.URI),
    CHALMERSIT(ChalmersitApiController.URI),
    WHITELIST(WhitelistApiController.URI);

    ApiKeyType(String uri) {
        this.URI = uri;
    }

    public final String URI;
}
