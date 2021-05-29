package it.chalmers.gamma.internal.client.service;

import it.chalmers.gamma.domain.ClientId;
import it.chalmers.gamma.domain.ClientSecret;
import it.chalmers.gamma.domain.EntityName;
import it.chalmers.gamma.internal.text.service.TextDTO;
import it.chalmers.gamma.util.domain.abstraction.DTO;

public record ClientDTO(ClientId clientId,
                        ClientSecret clientSecret,
                        String webServerRedirectUri,
                        boolean autoApprove,
                        EntityName name,
                        TextDTO description)
        implements DTO { }

