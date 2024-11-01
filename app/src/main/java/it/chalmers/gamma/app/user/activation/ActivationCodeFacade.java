package it.chalmers.gamma.app.user.activation;

import static it.chalmers.gamma.app.authentication.AccessGuard.isAdmin;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.user.activation.domain.UserActivation;
import it.chalmers.gamma.app.user.activation.domain.UserActivationRepository;
import it.chalmers.gamma.app.user.domain.Cid;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ActivationCodeFacade extends Facade {

  private final UserActivationRepository userActivationRepository;

  public ActivationCodeFacade(
      AccessGuard accessGuard, UserActivationRepository userActivationRepository) {
    super(accessGuard);
    this.userActivationRepository = userActivationRepository;
  }

  public List<UserActivationDTO> getAllUserActivations() {
    this.accessGuard.require(isAdmin());

    return this.userActivationRepository.getAll().stream().map(UserActivationDTO::new).toList();
  }

  public void removeUserActivation(String cid) {
    this.accessGuard.require(isAdmin());

    this.userActivationRepository.removeActivation(new Cid(cid));
  }

  public record UserActivationDTO(String cid, Instant createdAt) {
    public UserActivationDTO(UserActivation userActivation) {
      this(userActivation.cid().value(), userActivation.createdAt());
    }
  }
}
