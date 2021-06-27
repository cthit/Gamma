package it.chalmers.gamma.app.domain;

import io.soabase.recordbuilder.core.RecordBuilder;
import it.chalmers.gamma.adapter.secondary.jpa.util.DTO;

import java.time.Instant;

@RecordBuilder
public record Settings(Instant lastUpdatedUserAgreement) implements DTO, SettingsBuilder.With { }
