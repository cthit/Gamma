package it.chalmers.gamma.app.facade;

import it.chalmers.gamma.app.AccessGuard;
import it.chalmers.gamma.app.domain.user.Cid;
import it.chalmers.gamma.app.domain.useractivation.UserActivation;
import it.chalmers.gamma.app.port.repository.UserActivationRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ActivationCodeFacade extends Facade {

    private final UserActivationRepository userActivationRepository;

    public ActivationCodeFacade(AccessGuard accessGuard,
                                UserActivationRepository userActivationRepository) {
        super(accessGuard);
        this.userActivationRepository = userActivationRepository;
    }

    public record UserActivationDTO(String cid,
                                    String token,
                                    Instant createdAt) {
        public UserActivationDTO(UserActivation userActivation) {
            this(userActivation.cid().value(),
                    userActivation.token().value(),
                    userActivation.createdAt());
        }
    }

    public Optional<UserActivationDTO> get(String cid) {
        return this.userActivationRepository.get(new Cid(cid))
                .map(UserActivationDTO::new);
    }

    public List<UserActivationDTO> getAllUserActivations() {
        accessGuard.requireIsAdmin();
        return this.userActivationRepository.getAll()
                .stream()
                .map(UserActivationDTO::new)
                .toList();
    }

    public void removeUserActivation(String cid) {
        accessGuard.requireIsAdmin();
        this.userActivationRepository.removeActivation(new Cid(cid));
    }
}
