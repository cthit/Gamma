package it.chalmers.gamma.internal.client.service;

import it.chalmers.gamma.internal.text.data.dto.TextDTO;
import it.chalmers.gamma.util.domain.abstraction.DTO;

public record ClientUserAccessDTO (String name, TextDTO description) implements DTO {

    public ClientUserAccessDTO(ClientDTO client) {
        this(client.name(), client.description());
    }

}
