package it.chalmers.gamma.app.domain;

import java.util.List;
import java.util.Objects;

public record Client(ClientId clientId,
                     ClientSecret clientSecret,
                     String webServerRedirectUri,
                     boolean autoApprove,
                     PrettyName prettyName,
                     Text description,
                     List<AuthorityLevelName> restrictions) {

    public Client {
        //TODO Create Webserverredirecturi class instead of using string
        Objects.requireNonNull(clientId);
        Objects.requireNonNull(clientSecret);
        Objects.requireNonNull(webServerRedirectUri);
        Objects.requireNonNull(prettyName);
        Objects.requireNonNull(description);
        Objects.requireNonNull(restrictions);
    }

}

