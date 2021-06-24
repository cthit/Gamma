package it.chalmers.gamma.app.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.util.entity.DTO;

public record UserGDPRTraining(@JsonUnwrapped UserRestricted user, boolean gdpr) implements DTO { }
