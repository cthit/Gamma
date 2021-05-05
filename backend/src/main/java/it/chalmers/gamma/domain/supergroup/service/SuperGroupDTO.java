package it.chalmers.gamma.domain.supergroup.service;

import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.util.domain.Email;
import it.chalmers.gamma.domain.text.data.dto.TextDTO;

import java.util.Objects;

public record SuperGroupDTO(SuperGroupId id,
                     String name,
                     String prettyName,
                     SuperGroupType type,
                     Email email,
                     TextDTO description) implements DTO { }
