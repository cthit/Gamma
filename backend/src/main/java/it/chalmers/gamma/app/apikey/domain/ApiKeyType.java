package it.chalmers.gamma.app.apikey.domain;

import it.chalmers.gamma.adapter.primary.external.client.ClientApiV1Controller;
import it.chalmers.gamma.adapter.primary.external.goldapps.GoldappsV1ApiController;
import it.chalmers.gamma.adapter.primary.external.info.InfoV1ApiController;
import it.chalmers.gamma.adapter.primary.external.allowlist.AllowListV1ApiController;

public enum ApiKeyType {
    CLIENT(ClientApiV1Controller.URI),
    GOLDAPPS(GoldappsV1ApiController.URI),
    INFO(InfoV1ApiController.URI),
    ALLOW_LIST(AllowListV1ApiController.URI);

    public final String URI;

    ApiKeyType(String uri) {
        this.URI = uri;
    }
}
