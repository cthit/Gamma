package it.chalmers.gamma.app.apikey.domain;

import it.chalmers.gamma.adapter.primary.api.accountscaffold.AccountScaffoldV1ApiController;
import it.chalmers.gamma.adapter.primary.api.allowlist.AllowListV1ApiController;
import it.chalmers.gamma.adapter.primary.api.client.ClientApiV1Controller;
import it.chalmers.gamma.adapter.primary.api.info.InfoV1ApiController;

public enum ApiKeyType {
  CLIENT(ClientApiV1Controller.URI),
  ACCOUNT_SCAFFOLD(AccountScaffoldV1ApiController.URI),
  INFO(InfoV1ApiController.URI),
  ALLOW_LIST(AllowListV1ApiController.URI);

  public final String URI;

  ApiKeyType(String uri) {
    this.URI = uri;
  }
}
