package it.chalmers.gamma.app.facade;

import it.chalmers.gamma.app.usecase.AccessGuardUseCase;
import it.chalmers.gamma.app.domain.user.User;
import it.chalmers.gamma.app.domain.user.UserId;
import it.chalmers.gamma.app.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserGdprTrainingFacade extends Facade {

    private final UserRepository userRepository;

    public UserGdprTrainingFacade(AccessGuardUseCase accessGuard,
                                  UserRepository userRepository) {
        super(accessGuard);
        this.userRepository = userRepository;
    }

    public record UserGdprTrainedDTO(String cid,
                                     UUID id,
                                     String firstName,
                                     String lastName,
                                     String nick,
                                     boolean gdpr) {
        public UserGdprTrainedDTO(User user) {
            this(user.cid().value(),
                    user.id().value(),
                    user.firstName().value(),
                    user.lastName().value(),
                    user.nick().value(),
                    user.gdprTrained());
        }
    }

    public List<UserGdprTrainedDTO> getUsersWithGdprTrained() {
        accessGuard.requireIsAdmin();
        return this.userRepository.getAll()
                .stream()
                .map(UserGdprTrainedDTO::new)
                .toList();
    }

    public void updateGdprTrainedStatus(UUID userId, boolean gdprTrained) {
        accessGuard.requireIsAdmin();
        User oldUser = this.userRepository.get(new UserId(userId)).orElseThrow();
        this.userRepository.save(oldUser.withGdprTrained(gdprTrained));
    }

}
