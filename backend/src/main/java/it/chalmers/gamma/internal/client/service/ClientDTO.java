package it.chalmers.gamma.internal.client.service;

import it.chalmers.gamma.internal.text.data.dto.TextDTO;
import it.chalmers.gamma.util.domain.abstraction.DTO;

public record ClientDTO(ClientId clientId,
                 ClientSecret clientSecret,
                 String webServerRedirectUri,
                 boolean autoApprove,
                 String name,
                 TextDTO description)
        implements DTO { }

