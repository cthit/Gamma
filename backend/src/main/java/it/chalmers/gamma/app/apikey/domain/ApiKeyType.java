package it.chalmers.gamma.app.apikey.domain;

import it.chalmers.gamma.adapter.primary.external.v1.ClientApiV1Controller;
import it.chalmers.gamma.adapter.primary.external.chalmersit.InfoApiController;
import it.chalmers.gamma.adapter.primary.external.goldapps.GoldappsApiController;
import it.chalmers.gamma.adapter.primary.external.whitelist.WhitelistApiController;

public enum ApiKeyType {
    CLIENT(ClientApiV1Controller.URI),
    GOLDAPPS(GoldappsApiController.URI),
    INFO(InfoApiController.URI),
    WHITELIST(WhitelistApiController.URI);

    ApiKeyType(String uri) {
        this.URI = uri;
    }

    public final String URI;
}
