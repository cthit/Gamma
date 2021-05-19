package it.chalmers.gamma.internal.user.gdpr.service;

import it.chalmers.gamma.internal.user.service.UserId;
import it.chalmers.gamma.internal.user.service.UserFinder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserGDPRTrainingService {

    private final UserGDPRTrainingRepository repository;
    private final UserFinder userFinder;

    public UserGDPRTrainingService(UserGDPRTrainingRepository repository,
                                   UserFinder userFinder) {
        this.repository = repository;
        this.userFinder = userFinder;
    }

    public List<UserGDPRTrainingDTO> getUsersWithGDPR() {
        return this.userFinder.getAll()
                .stream()
                .map(user -> new UserGDPRTrainingDTO(user, false))
                .collect(Collectors.toList());
    }

    public void editGDPR(UserId userId, boolean gdprValue) {
        if(gdprValue) {
            this.repository.save(new UserGDPRTraining(userId));
        } else {
            this.repository.deleteById(userId);
        }
    }

}
