package it.chalmers.gamma.domain.client.service;

import it.chalmers.gamma.domain.text.data.dto.TextDTO;
import it.chalmers.gamma.util.domain.abstraction.DTO;

import java.util.Objects;

public record ClientUserAccessDTO (String name, TextDTO description) implements DTO {

    public ClientUserAccessDTO(ClientDTO client) {
        this(client.name(), client.description());
    }

}
