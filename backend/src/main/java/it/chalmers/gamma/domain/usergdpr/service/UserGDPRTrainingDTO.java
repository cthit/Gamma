package it.chalmers.gamma.domain.usergdpr.service;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.domain.user.service.UserRestrictedDTO;
import it.chalmers.gamma.util.domain.abstraction.DTO;

public record UserGDPRTrainingDTO(UserRestrictedDTO user, boolean gdpr) implements DTO { }
