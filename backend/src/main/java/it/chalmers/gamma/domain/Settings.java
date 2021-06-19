package it.chalmers.gamma.domain;

import it.chalmers.gamma.util.domain.abstraction.DTO;

import java.time.Instant;

public record Settings(Instant lastUpdatedUserAgreement) implements DTO { }
