package it.chalmers.gamma.domain;

import it.chalmers.gamma.util.domain.abstraction.DTO;

import java.util.List;

public record ClientRestrictions(ClientId clientId,
                                 List<AuthorityLevelName> authorityLevelNameList) implements DTO { }
