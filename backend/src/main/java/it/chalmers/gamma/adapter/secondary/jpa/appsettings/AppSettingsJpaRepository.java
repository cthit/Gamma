package it.chalmers.gamma.adapter.secondary.jpa.appsettings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppSettingsJpaRepository extends JpaRepository<AppSettingsEntity, Integer> {

    public AppSettingsEntity findTopByOrderByIdDesc();

}
