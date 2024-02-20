package it.chalmers.gamma.app.user;

import static it.chalmers.gamma.app.authentication.AccessGuard.isAdmin;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import it.chalmers.gamma.app.user.gdpr.GdprTrainedRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UserGdprTrainingFacade extends Facade {

  private final GdprTrainedRepository gdprTrainedRepository;
  private final UserRepository userRepository;

  public UserGdprTrainingFacade(
      AccessGuard accessGuard,
      GdprTrainedRepository gdprTrainedRepository,
      UserRepository userRepository) {
    super(accessGuard);
    this.gdprTrainedRepository = gdprTrainedRepository;
    this.userRepository = userRepository;
  }

  public List<UUID> getGdprTrained() {
    this.accessGuard.require(isAdmin());

    return this.gdprTrainedRepository.getAll().stream().map(UserId::value).toList();
  }

  public void updateGdprTrainedStatus(UUID userId, boolean gdprTrained) {
    this.accessGuard.require(isAdmin());

    this.gdprTrainedRepository.setGdprTrainedStatus(new UserId(userId), gdprTrained);
  }
}
