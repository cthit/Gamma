package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.app.domain.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGDPRTrainingJpaRepository extends JpaRepository<UserGDPRTrainingEntity, UserId> { }
