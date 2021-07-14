package it.chalmers.gamma.app.user;

import it.chalmers.gamma.adapter.secondary.jpa.user.UserGDPRTrainingEntity;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserGDPRTrainingJpaRepository;
import it.chalmers.gamma.app.domain.UserGDPRTraining;
import it.chalmers.gamma.app.domain.UserId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserGDPRTrainingService {

    private final UserGDPRTrainingJpaRepository repository;
    private final UserService userService;

    public UserGDPRTrainingService(UserGDPRTrainingJpaRepository repository,
                                   UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    public List<UserGDPRTraining> getUsersWithGDPR() {
        List<UserId> gdprTrained = this.repository.findAll()
                .stream()
                .map(UserGDPRTrainingEntity::toDomain)
                .collect(Collectors.toList());

        return this.userService.getAll()
                .stream()
                .map(user -> new UserGDPRTraining(user, gdprTrained.remove(user.id())))
                .collect(Collectors.toList());
    }

    public void editGDPR(UserId userId, boolean gdprValue) {
        if(gdprValue) {
            this.repository.save(new UserGDPRTrainingEntity(userId));
        } else {
            this.repository.deleteById(userId);
        }
    }

}
