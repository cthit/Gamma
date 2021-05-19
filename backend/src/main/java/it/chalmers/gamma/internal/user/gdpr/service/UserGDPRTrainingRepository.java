package it.chalmers.gamma.internal.user.gdpr.service;

import it.chalmers.gamma.internal.user.service.UserId;
import org.springframework.data.repository.CrudRepository;

public interface UserGDPRTrainingRepository extends CrudRepository<UserGDPRTraining, UserId> { }
