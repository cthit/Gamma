package it.chalmers.gamma.app.apikey.domain;

import it.chalmers.gamma.app.supergroup.domain.SuperGroupType;
import java.util.List;

public record ApiKeyScopeSettings(int version, List<SuperGroupTypeConfig> superGroupTypeConfigs) {

  public record SuperGroupTypeConfig(SuperGroupType type, boolean gdprFilter) {}
}
