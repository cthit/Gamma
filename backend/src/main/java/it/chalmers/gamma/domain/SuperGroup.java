package it.chalmers.gamma.domain;

import it.chalmers.gamma.util.entity.DTO;

public record SuperGroup(SuperGroupId id,
                         EntityName name,
                         PrettyName prettyName,
                         SuperGroupType type,
                         Email email,
                         Text description) implements DTO { }
