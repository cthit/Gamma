package it.chalmers.gamma.app.user;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.user.domain.GammaUser;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.gdpr.GdprTrainedRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static it.chalmers.gamma.app.authentication.AccessGuard.isAdmin;

@Service
public class UserGdprTrainingFacade extends Facade {

    private final GdprTrainedRepository gdprTrainedRepository;

    public UserGdprTrainingFacade(AccessGuard accessGuard, GdprTrainedRepository gdprTrainedRepository) {
        super(accessGuard);
        this.gdprTrainedRepository = gdprTrainedRepository;
    }

    public List<UserGdprTrainedDTO> getUsersWithGdprTrained() {
        this.accessGuard.require(isAdmin());

        throw new UnsupportedOperationException();
    }

    public void updateGdprTrainedStatus(UUID userId, boolean gdprTrained) {
        this.accessGuard.require(isAdmin());

        throw new UnsupportedOperationException();
    }

    public record UserGdprTrainedDTO(String cid,
                                     UUID id,
                                     String firstName,
                                     String lastName,
                                     String nick,
                                     boolean gdpr) {
        public UserGdprTrainedDTO(GammaUser user, boolean gdprTrained) {
            this(user.cid().value(),
                    user.id().value(),
                    user.firstName().value(),
                    user.lastName().value(),
                    user.nick().value(),
                    gdprTrained);
        }
    }

}
