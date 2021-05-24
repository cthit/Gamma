package it.chalmers.gamma.internal.supergroup.service;

import it.chalmers.gamma.domain.Name;
import it.chalmers.gamma.domain.PrettyName;
import it.chalmers.gamma.domain.SuperGroupId;
import it.chalmers.gamma.domain.SuperGroupType;
import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.domain.Email;
import it.chalmers.gamma.internal.text.service.TextDTO;

public record SuperGroupDTO(SuperGroupId id,
                            Name name,
                            PrettyName prettyName,
                            SuperGroupType type,
                            Email email,
                            TextDTO description) implements DTO { }
