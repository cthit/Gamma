package it.chalmers.gamma.app.apikey.domain.settings;

import it.chalmers.gamma.app.supergroup.domain.SuperGroupType;
import java.util.List;

public record ApiKeyAccountScaffoldSettings(int version, List<SuperGroupType> superGroupTypes) {}
