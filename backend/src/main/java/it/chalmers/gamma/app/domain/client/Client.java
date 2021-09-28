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
public record Client(ClientId clientId,
                     ClientSecret clientSecret,
                     WebServerRedirectUrl webServerRedirectUrl,
                     boolean autoApprove,
                     PrettyName prettyName,
                     Text description,
                     List<AuthorityLevelName> restrictions,
                     List<User> approvedUsers,
                     Optional<ApiKey> clientApiKey) implements ClientBuilder.With {

    public Client {
        //TODO Create Webserverredirecturi class instead of using string
        Objects.requireNonNull(clientId);
        Objects.requireNonNull(webServerRedirectUrl);
        Objects.requireNonNull(prettyName);
        Objects.requireNonNull(description);
        Objects.requireNonNull(restrictions);
        Objects.requireNonNull(approvedUsers);
    }

}

