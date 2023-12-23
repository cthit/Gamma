package it.chalmers.gamma.adapter.secondary.jpa.settings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SettingsJpaRepository extends JpaRepository<SettingsEntity, Integer> {

    Optional<SettingsEntity> findTopByOrderByVersionDesc();

}
