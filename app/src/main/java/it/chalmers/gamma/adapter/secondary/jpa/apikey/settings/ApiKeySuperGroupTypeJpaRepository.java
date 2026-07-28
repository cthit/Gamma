package it.chalmers.gamma.adapter.secondary.jpa.apikey.settings;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiKeySuperGroupTypeJpaRepository
    extends JpaRepository<ApiKeySuperGroupTypeEntity, ApiKeySuperGroupTypePK> {

  List<ApiKeySuperGroupTypeEntity> findById_ApiKeyId(UUID apiKeyId);

  void deleteById_ApiKeyId(UUID apiKeyId);
}
