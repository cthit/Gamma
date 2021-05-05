package it.chalmers.gamma.internal.group.service;

import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.util.domain.Email;

public record GroupMinifiedDTO (String name, Email email, GroupId id, String prettyName) implements DTO {
    public GroupMinifiedDTO(GroupDTO g) {
        this(g.name(), g.email(), g.id(), g.prettyName());
    }
}
