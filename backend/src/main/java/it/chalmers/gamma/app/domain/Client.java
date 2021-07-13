package it.chalmers.gamma.app.domain;

import it.chalmers.gamma.adapter.secondary.jpa.util.DTO;

import java.util.Objects;

public record Client(ClientId clientId,
                     ClientSecret clientSecret,
                     String webServerRedirectUri,
                     boolean autoApprove,
                     PrettyName prettyName,
                     Text description)
        implements DTO {

    public Client {
        //TODO Create Webserverredirecturi class instead of using string
        Objects.requireNonNull(clientId);
        Objects.requireNonNull(clientSecret);
        Objects.requireNonNull(webServerRedirectUri);
        Objects.requireNonNull(prettyName);
        Objects.requireNonNull(description);
    }

}

