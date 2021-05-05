package it.chalmers.gamma.internal.supergroup.service;

import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.util.domain.Email;
import it.chalmers.gamma.internal.text.data.dto.TextDTO;

public record SuperGroupDTO(SuperGroupId id,
                     String name,
                     String prettyName,
                     SuperGroupType type,
                     Email email,
                     TextDTO description) implements DTO { }
