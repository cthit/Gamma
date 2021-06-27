package it.chalmers.gamma.app.group.service;

import it.chalmers.gamma.app.domain.GroupId;
import it.chalmers.gamma.app.domain.EntityName;
import it.chalmers.gamma.app.domain.PrettyName;
import it.chalmers.gamma.adapter.secondary.jpa.util.DTO;
import it.chalmers.gamma.app.domain.Email;
import it.chalmers.gamma.app.domain.SuperGroupId;

public record GroupShallowDTO(GroupId id,
                              Email email,
                              EntityName name,
                              PrettyName prettyName,
                              SuperGroupId superGroupId) implements DTO {
}
