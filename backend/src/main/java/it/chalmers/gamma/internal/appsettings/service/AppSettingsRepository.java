package it.chalmers.gamma.internal.appsettings.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppSettingsRepository extends JpaRepository<AppSettingsEntity, Integer> {

    public AppSettingsEntity findTopByOrderByIdDesc();

}
