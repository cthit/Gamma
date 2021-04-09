package it.chalmers.gamma.domain.usergdpr.service;

import it.chalmers.gamma.domain.user.service.UserId;
import org.springframework.data.repository.CrudRepository;

public interface UserGDPRTrainingRepository extends CrudRepository<UserGDPRTraining, UserId> { }
