package it.chalmers.gamma.domain;

import it.chalmers.gamma.util.domain.abstraction.DTO;

public record UserGDPRTraining(UserRestricted user, boolean gdpr) implements DTO { }
