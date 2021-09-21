package it.chalmers.gamma.app.user;

import it.chalmers.gamma.app.AccessGuard;
import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.domain.user.Cid;
import it.chalmers.gamma.domain.user.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class UserFacade extends Facade {

    private final UserRepository userRepository;

    public UserFacade(AccessGuard accessGuard, UserRepository userRepository) {
        super(accessGuard);
        this.userRepository = userRepository;
    }

    public record UserDTO(String cid,
                          String nick,
                          String firstName,
                          String lastName,
                          UUID id,
                          int acceptanceYear) {
        public UserDTO(User user) {
            this(user.cid().value(),
                    user.nick().value(),
                    user.firstName().value(),
                    user.lastName().value(),
                    user.id().value(),
                    user.acceptanceYear().value());
        }
    }

    public Optional<UserDTO> get(String cid) {
        this.accessGuard.requireSignedIn();
        return this.userRepository.get(new Cid(cid))
                .map(UserDTO::new);
    }

    public List<UserDTO> getAll() {
        this.accessGuard.requireSignedIn();
        return this.userRepository.getAll().stream().map(UserDTO::new).toList();
    }
}
