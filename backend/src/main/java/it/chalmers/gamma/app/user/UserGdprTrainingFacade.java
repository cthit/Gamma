package it.chalmers.gamma.app.user;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.user.domain.User;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static it.chalmers.gamma.app.authentication.AccessGuard.isAdmin;

@Service
public class UserGdprTrainingFacade extends Facade {

    private final UserRepository userRepository;

    public UserGdprTrainingFacade(AccessGuard accessGuard,
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
                    user.extended().gdprTrained());
        }
    }

    public List<UserGdprTrainedDTO> getUsersWithGdprTrained() {
        this.accessGuard.require(isAdmin());

        return this.userRepository.getAll()
                .stream()
                .map(UserGdprTrainedDTO::new)
                .toList();
    }

    public void updateGdprTrainedStatus(UUID userId, boolean gdprTrained) {
        this.accessGuard.require(isAdmin());

        User oldUser = this.userRepository.get(new UserId(userId)).orElseThrow();
        User newUser = oldUser.withExtended(oldUser.extended().withGdprTrained(gdprTrained));

        this.userRepository.save(newUser);
    }

}
