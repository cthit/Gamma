package it.chalmers.gamma.domain;

import it.chalmers.gamma.util.entity.DTO;

public record Group(GroupId id,
                    Email email,
                    EntityName name,
                    PrettyName prettyName,
                    SuperGroup superGroup)
        implements DTO { }
