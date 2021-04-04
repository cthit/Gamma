package it.chalmers.gamma.domain.usergdpr.service;

import it.chalmers.gamma.domain.user.UserId;
import it.chalmers.gamma.domain.user.data.dto.UserRestrictedDTO;
import it.chalmers.gamma.domain.user.service.UserFinder;
import it.chalmers.gamma.domain.usergdpr.data.UserGDPRTraining;
import it.chalmers.gamma.domain.usergdpr.data.UserGDPRTrainingDTO;
import it.chalmers.gamma.domain.usergdpr.data.UserGDPRTrainingRepository;
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
                .map(user -> new UserGDPRTrainingDTO(new UserRestrictedDTO(user), false))

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
