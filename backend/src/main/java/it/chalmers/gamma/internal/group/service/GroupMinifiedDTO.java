package it.chalmers.gamma.internal.group.service;

import it.chalmers.gamma.domain.GroupId;
import it.chalmers.gamma.domain.Name;
import it.chalmers.gamma.domain.PrettyName;
import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.domain.Email;

public record GroupMinifiedDTO (Name name, Email email, GroupId id, PrettyName prettyName) implements DTO {
    public GroupMinifiedDTO(GroupDTO g) {
        this(g.name(), g.email(), g.id(), g.prettyName());
    }
}
