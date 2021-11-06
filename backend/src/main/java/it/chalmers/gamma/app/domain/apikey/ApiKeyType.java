package it.chalmers.gamma.app.domain.apikey;

import it.chalmers.gamma.adapter.primary.api.legacy.LegacyApiController;
import it.chalmers.gamma.adapter.primary.api.v1.ClientApiV1Controller;
import it.chalmers.gamma.adapter.primary.api.chalmersit.ChalmersitApiController;
import it.chalmers.gamma.adapter.primary.api.goldapps.GoldappsApiController;
import it.chalmers.gamma.adapter.primary.api.whitelist.WhitelistApiController;

public enum ApiKeyType {
    CLIENT(ClientApiV1Controller.URI),
    GOLDAPPS(GoldappsApiController.URI),
    CHALMERSIT(ChalmersitApiController.URI),
    WHITELIST(WhitelistApiController.URI),
    LEGACY(LegacyApiController.URI);

    ApiKeyType(String uri) {
        this.URI = uri;
    }

    public final String URI;
}
