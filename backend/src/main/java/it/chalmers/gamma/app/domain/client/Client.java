package it.chalmers.gamma.app.domain.client;

import io.soabase.recordbuilder.core.RecordBuilder;
import it.chalmers.gamma.app.domain.common.PrettyName;
import it.chalmers.gamma.app.domain.common.Text;
import it.chalmers.gamma.app.domain.apikey.ApiKey;
import it.chalmers.gamma.app.domain.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.app.domain.user.User;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RecordBuilder
public record Client(ClientUid clientUid,
                     ClientId clientId,
                     ClientSecret clientSecret,
                     WebServerRedirectUrl webServerRedirectUrl,
                     PrettyName prettyName,
                     Text description,
                     List<AuthorityLevelName> restrictions,
                     List<Scope> scopes,
                     List<User> approvedUsers,
                     Optional<ApiKey> clientApiKey) implements ClientBuilder.With {

    public Client {
        Objects.requireNonNull(clientId);
        Objects.requireNonNull(webServerRedirectUrl);
        Objects.requireNonNull(prettyName);
        Objects.requireNonNull(description);
        Objects.requireNonNull(restrictions);
        Objects.requireNonNull(scopes);
        Objects.requireNonNull(approvedUsers);
        Objects.requireNonNull(clientApiKey);
    }

}

