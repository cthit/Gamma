package it.chalmers.gamma.app.usergdpr.service;

import it.chalmers.gamma.app.domain.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGDPRTrainingRepository extends JpaRepository<UserGDPRTrainingEntity, UserId> { }
