package it.chalmers.gamma.internal.usergdpr.service;

import it.chalmers.gamma.domain.UserId;
import org.springframework.data.repository.CrudRepository;

public interface UserGDPRTrainingRepository extends CrudRepository<UserGDPRTrainingEntity, UserId> { }
