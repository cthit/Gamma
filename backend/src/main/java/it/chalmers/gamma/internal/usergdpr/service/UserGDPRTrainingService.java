package it.chalmers.gamma.internal.usergdpr.service;

import it.chalmers.gamma.domain.UserGDPRTraining;
import it.chalmers.gamma.domain.UserId;
import it.chalmers.gamma.internal.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserGDPRTrainingService {

    private final UserGDPRTrainingRepository repository;
    private final UserService userService;

    public UserGDPRTrainingService(UserGDPRTrainingRepository repository,
                                   UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    public List<UserGDPRTraining> getUsersWithGDPR() {
        List<UserId> gdprTrained = this.repository.findAll()
                .stream()
                .map(UserGDPRTrainingEntity::toDTO)
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
