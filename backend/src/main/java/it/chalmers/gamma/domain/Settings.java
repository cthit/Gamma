package it.chalmers.gamma.domain;

import io.soabase.recordbuilder.core.RecordBuilder;
import it.chalmers.gamma.util.entity.DTO;

import java.time.Instant;

@RecordBuilder
public record Settings(Instant lastUpdatedUserAgreement) implements DTO, SettingsBuilder.With { }
