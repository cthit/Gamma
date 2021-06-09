package it.chalmers.gamma.internal.usergdpr.service;

import it.chalmers.gamma.domain.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGDPRTrainingRepository extends JpaRepository<UserGDPRTrainingEntity, UserId> { }
