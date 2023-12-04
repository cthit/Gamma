package it.chalmers.gamma.app.user;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.user.domain.GammaUser;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import it.chalmers.gamma.app.user.gdpr.GdprTrainedRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static it.chalmers.gamma.app.authentication.AccessGuard.isAdmin;

@Service
public class UserGdprTrainingFacade extends Facade {

    private final GdprTrainedRepository gdprTrainedRepository;
    private final UserRepository userRepository;

    public UserGdprTrainingFacade(AccessGuard accessGuard, GdprTrainedRepository gdprTrainedRepository, UserRepository userRepository) {
        super(accessGuard);
        this.gdprTrainedRepository = gdprTrainedRepository;
        this.userRepository = userRepository;
    }

    public List<UserGdprTrainedDTO> getUsersWithGdprTrained() {
        this.accessGuard.require(isAdmin());

        return this.userRepository.getAll().stream().map(UserGdprTrainedDTO::new).toList();
    }

    public void updateGdprTrainedStatus(UUID userId, boolean gdprTrained) {
        this.accessGuard.require(isAdmin());

        this.gdprTrainedRepository.setGdprTrainedStatus(new UserId(userId), gdprTrained);
    }


    public record UserGdprTrainedDTO(String cid,
                                     UUID id,
                                     String firstName,
                                     String lastName,
                                     String nick,
                                     boolean gdpr) {
        public UserGdprTrainedDTO(GammaUser user) {
            this(user.cid().value(),
                    user.id().value(),
                    user.firstName().value(),
                    user.lastName().value(),
                    user.nick().value(),
                    user.extended().gdpr());
        }
    }

}
