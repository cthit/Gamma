package it.chalmers.gamma.app.domain;

import it.chalmers.gamma.adapter.secondary.jpa.util.DTO;

public record Group(GroupId id,
                    Email email,
                    EntityName name,
                    PrettyName prettyName,
                    SuperGroup superGroup)
        implements DTO { }
