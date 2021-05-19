package it.chalmers.gamma.internal.client.restriction.service;

import it.chalmers.gamma.internal.authority.level.service.AuthorityLevelName;
import it.chalmers.gamma.internal.client.service.ClientId;
import it.chalmers.gamma.util.domain.abstraction.DTO;

import java.util.List;

public record ClientRestrictionDTO(ClientId clientId,
                                   List<AuthorityLevelName> authorityLevelNameList) implements DTO { }
