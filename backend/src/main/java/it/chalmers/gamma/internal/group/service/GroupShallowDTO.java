package it.chalmers.gamma.internal.group.service;

import it.chalmers.gamma.domain.GroupId;
import it.chalmers.gamma.domain.Name;
import it.chalmers.gamma.domain.PrettyName;
import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.domain.Email;
import it.chalmers.gamma.domain.SuperGroupId;

public record GroupShallowDTO(GroupId id,
                              Email email,
                              Name name,
                              PrettyName prettyName,
                              SuperGroupId superGroupId) implements DTO {
}
