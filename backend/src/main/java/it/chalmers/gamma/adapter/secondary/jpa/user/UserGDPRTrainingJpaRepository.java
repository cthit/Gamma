package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.domain.user.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGDPRTrainingJpaRepository extends JpaRepository<UserGDPRTrainingEntity, UserId> { }
