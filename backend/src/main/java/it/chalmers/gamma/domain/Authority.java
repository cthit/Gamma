package it.chalmers.gamma.domain;

import it.chalmers.gamma.internal.authority.service.AuthorityType;

public record Authority(AuthorityLevelName authority, AuthorityType type) { }