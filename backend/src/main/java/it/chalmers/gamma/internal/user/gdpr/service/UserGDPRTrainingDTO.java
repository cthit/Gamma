package it.chalmers.gamma.internal.user.gdpr.service;

import it.chalmers.gamma.internal.user.service.UserRestrictedDTO;
import it.chalmers.gamma.util.domain.abstraction.DTO;

public record UserGDPRTrainingDTO(UserRestrictedDTO user, boolean gdpr) implements DTO { }