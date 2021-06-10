package it.chalmers.gamma.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.List;

public record ClientWithRestrictions(@JsonUnwrapped Client client, List<AuthorityLevelName> restrictions) { }
