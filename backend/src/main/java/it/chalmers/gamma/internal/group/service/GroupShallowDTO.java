package it.chalmers.gamma.internal.group.service;

import it.chalmers.gamma.domain.GroupId;
import it.chalmers.gamma.domain.EntityName;
import it.chalmers.gamma.domain.PrettyName;
import it.chalmers.gamma.util.entity.DTO;
import it.chalmers.gamma.domain.Email;
import it.chalmers.gamma.domain.SuperGroupId;

public record GroupShallowDTO(GroupId id,
                              Email email,
                              EntityName name,
                              PrettyName prettyName,
                              SuperGroupId superGroupId) implements DTO {
}
