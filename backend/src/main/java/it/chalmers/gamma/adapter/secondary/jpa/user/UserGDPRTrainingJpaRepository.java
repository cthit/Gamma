package it.chalmers.gamma.adapter.secondary.jpa.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserGDPRTrainingJpaRepository extends JpaRepository<UserGDPRTrainingEntity, UUID> { }
