package it.chalmers.gamma.app.client.domain;

import io.soabase.recordbuilder.core.RecordBuilder;
import it.chalmers.gamma.app.apikey.domain.ApiKey;
import it.chalmers.gamma.app.apikey.domain.ApiKeyType;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.common.Text;
import it.chalmers.gamma.app.user.domain.User;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RecordBuilder
public record Client(ClientUid clientUid,
                     ClientId clientId,
                     ClientSecret clientSecret,
                     RedirectUrl redirectUrl,
                     PrettyName prettyName,
                     Text description,
                     List<AuthorityLevelName> restrictions,
                     List<Scope> scopes,
                     List<User> approvedUsers,
                     //TODO: Don't use a optional
                     Optional<ApiKey> clientApiKey) implements ClientBuilder.With {

    public Client {
        Objects.requireNonNull(clientId);
        Objects.requireNonNull(redirectUrl);
        Objects.requireNonNull(prettyName);
        Objects.requireNonNull(description);
        Objects.requireNonNull(restrictions);
        Objects.requireNonNull(scopes);
        Objects.requireNonNull(approvedUsers);
        Objects.requireNonNull(clientApiKey);


        if (clientApiKey.isPresent() && clientApiKey.get().keyType() != ApiKeyType.CLIENT) {
            throw new IllegalArgumentException("If a client has a ApiKey, then the type must be ApiKeyType.CLIENT");
        }
    }

}

