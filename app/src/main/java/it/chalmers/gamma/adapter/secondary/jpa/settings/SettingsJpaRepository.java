package it.chalmers.gamma.adapter.secondary.jpa.settings;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingsJpaRepository extends JpaRepository<SettingsEntity, Integer> {

  Optional<SettingsEntity> findTopByOrderByVersionDesc();
}
