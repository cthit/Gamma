package it.chalmers.gamma.domain.usergdpr.data;

import it.chalmers.gamma.domain.user.UserId;
import org.springframework.data.repository.CrudRepository;

public interface UserGDPRTrainingRepository extends CrudRepository<UserGDPRTraining, UserId> { }
