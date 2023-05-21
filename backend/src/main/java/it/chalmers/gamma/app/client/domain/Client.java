package it.chalmers.gamma.app.client.domain;

import it.chalmers.gamma.app.apikey.domain.ApiKey;
import it.chalmers.gamma.app.apikey.domain.ApiKeyType;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.common.Text;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class Client {
    private final ClientUid clientUid;
    private final ClientId clientId;
    private final ClientSecret clientSecret;
    private final ClientRedirectUrl clientRedirectUrl;
    private final PrettyName prettyName;
    private final Text description;
    private final List<Scope> scopes;
    private final ApiKey clientApiKey;
    private final ClientOwner owner;

    public Client(ClientUid clientUid,
                  ClientId clientId,
                  ClientSecret clientSecret,
                  ClientRedirectUrl clientRedirectUrl,
                  PrettyName prettyName,
                  Text description,
                  List<Scope> scopes,
                  ApiKey clientApiKey,
                  ClientOwner owner) {
        Objects.requireNonNull(clientId);
        Objects.requireNonNull(clientRedirectUrl);
        Objects.requireNonNull(prettyName);
        Objects.requireNonNull(description);
        Objects.requireNonNull(scopes);
        Objects.requireNonNull(owner);

        if (clientApiKey != null && clientApiKey.keyType() != ApiKeyType.CLIENT) {
            throw new IllegalArgumentException("If a client has a ApiKey, then the type must be ApiKeyType.CLIENT");
        }

        this.clientUid = clientUid;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.clientRedirectUrl = clientRedirectUrl;
        this.prettyName = prettyName;
        this.description = description;
        this.scopes = scopes;
        this.clientApiKey = clientApiKey;
        this.owner = owner;
    }

    public ClientUid clientUid() {
        return clientUid;
    }

    public ClientId clientId() {
        return clientId;
    }

    public ClientSecret clientSecret() {
        return clientSecret;
    }

    public ClientRedirectUrl clientRedirectUrl() {
        return clientRedirectUrl;
    }

    public PrettyName prettyName() {
        return prettyName;
    }

    public Text description() {
        return description;
    }

    public List<Scope> scopes() {
        return scopes;
    }

    public Optional<ApiKey> clientApiKey() {
        return Optional.ofNullable(this.clientApiKey);
    }

    public ClientOwner access() {
        return owner;
    }

    public Client withClientSecret(ClientSecret clientSecret) {
        return new Client(
                this.clientUid,
                this.clientId,
                clientSecret,
                this.clientRedirectUrl,
                this.prettyName,
                this.description,
                this.scopes,
                this.clientApiKey,
                this.owner
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return clientUid.equals(client.clientUid)
                && clientId.equals(client.clientId)
                && clientSecret.equals(client.clientSecret)
                && clientRedirectUrl.equals(client.clientRedirectUrl)
                && prettyName.equals(client.prettyName)
                && description.equals(client.description)
                && scopes.equals(client.scopes)
                && Objects.equals(clientApiKey, client.clientApiKey)
                && owner == client.owner;
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientUid,
                clientId,
                clientSecret,
                clientRedirectUrl,
                prettyName,
                description,
                scopes,
                clientApiKey,
                owner
        );
    }

    @Override
    public String toString() {
        return "Client{" +
                "clientUid=" + clientUid +
                ", clientId=" + clientId +
                ", clientRedirectUrl=" + clientRedirectUrl +
                ", prettyName=" + prettyName +
                ", description=" + description +
                ", scopes=" + scopes +
                ", clientApiKey=" + clientApiKey +
                ", access=" + owner +
                '}';
    }
}

