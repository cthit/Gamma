package it.chalmers.gamma.domain;

import it.chalmers.gamma.util.domain.abstraction.DTO;

public record Client(ClientId clientId,
                     String webServerRedirectUri,
                     boolean autoApprove,
                     PrettyName prettyName,
                     Text description)
        implements DTO { }

