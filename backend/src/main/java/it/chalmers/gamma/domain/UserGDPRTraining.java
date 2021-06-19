package it.chalmers.gamma.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.util.domain.abstraction.DTO;

public record UserGDPRTraining(@JsonUnwrapped UserRestricted user, boolean gdpr) implements DTO { }
