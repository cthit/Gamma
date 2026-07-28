package it.chalmers.gamma.adapter.secondary.jpa.apikey.settings;

import it.chalmers.gamma.app.apikey.domain.ApiKeySuperGroupTypeRepository;
import it.chalmers.gamma.app.apikey.domain.ApiKeyScopeSettings.SuperGroupTypeConfig;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupType;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class ApiKeySuperGroupTypeRepositoryAdapter implements ApiKeySuperGroupTypeRepository {

  private final ApiKeySuperGroupTypeJpaRepository jpaRepository;

  public ApiKeySuperGroupTypeRepositoryAdapter(
      ApiKeySuperGroupTypeJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public List<SuperGroupTypeConfig> get(UUID apiKeyId) {
    return this.jpaRepository.findById_ApiKeyId(apiKeyId).stream()
        .map(e -> new SuperGroupTypeConfig(
            new SuperGroupType(e.getId().superGroupTypeName), e.isGdprFilter()))
        .toList();
  }

  @Transactional
  @Override
  public void set(UUID apiKeyId, List<SuperGroupTypeConfig> configs) {
    this.jpaRepository.deleteById_ApiKeyId(apiKeyId);
    this.jpaRepository.flush();

    for (var config : configs) {
      var pk = new ApiKeySuperGroupTypePK(apiKeyId, config.type().value());
      this.jpaRepository.save(new ApiKeySuperGroupTypeEntity(pk, config.gdprFilter()));
    }
  }
}
