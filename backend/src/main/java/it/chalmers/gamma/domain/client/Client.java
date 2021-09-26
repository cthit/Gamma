package it.chalmers.gamma.domain.client;

import io.soabase.recordbuilder.core.RecordBuilder;
import it.chalmers.gamma.domain.common.PrettyName;
import it.chalmers.gamma.domain.common.Text;
import it.chalmers.gamma.domain.apikey.ApiKey;
import it.chalmers.gamma.domain.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.domain.user.User;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RecordBuilder
public record Client(ClientId clientId,
                     ClientSecret clientSecret,
                     String webServerRedirectUri,
                     boolean autoApprove,
                     PrettyName prettyName,
                     Text description,
                     List<AuthorityLevelName> restrictions,
                     List<User> approvedUsers,
                     Optional<ApiKey> clientApiKey) implements ClientBuilder.With {

    public Client {
        //TODO Create Webserverredirecturi class instead of using string
        Objects.requireNonNull(clientId);
        Objects.requireNonNull(webServerRedirectUri);
        Objects.requireNonNull(prettyName);
        Objects.requireNonNull(description);
        Objects.requireNonNull(restrictions);
        Objects.requireNonNull(approvedUsers);
    }

}

