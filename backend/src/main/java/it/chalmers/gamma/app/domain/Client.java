package it.chalmers.gamma.app.domain;

import it.chalmers.gamma.adapter.secondary.jpa.util.DTO;

public record Client(ClientId clientId,
                     String webServerRedirectUri,
                     boolean autoApprove,
                     PrettyName prettyName,
                     Text description)
        implements DTO { }

