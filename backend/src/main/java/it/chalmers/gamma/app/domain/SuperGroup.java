package it.chalmers.gamma.app.domain;

import it.chalmers.gamma.adapter.secondary.jpa.util.DTO;

public record SuperGroup(SuperGroupId id,
                         EntityName name,
                         PrettyName prettyName,
                         SuperGroupType type,
                         Email email,
                         Text description) implements DTO { }
