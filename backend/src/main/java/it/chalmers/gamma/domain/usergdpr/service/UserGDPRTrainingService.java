package it.chalmers.gamma.domain.usergdpr.service;

import it.chalmers.gamma.domain.user.UserId;
import it.chalmers.gamma.domain.user.data.UserRestrictedDTO;
import it.chalmers.gamma.domain.user.exception.UserNotFoundException;
import it.chalmers.gamma.domain.user.service.UserFinder;
import it.chalmers.gamma.domain.user.service.UserService;
import it.chalmers.gamma.domain.usergdpr.data.UserGDPRTraining;
import it.chalmers.gamma.domain.usergdpr.data.UserGDPRTrainingDTO;
import it.chalmers.gamma.domain.usergdpr.data.UserGDPRTrainingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserGDPRTrainingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserGDPRTrainingService.class);

    private final UserGDPRTrainingRepository repository;
    private final UserService userService;
    private final UserFinder userFinder;

    public UserGDPRTrainingService(UserGDPRTrainingRepository repository,
                                   UserService userService,
                                   UserFinder userFinder) {
        this.repository = repository;
        this.userService = userService;
        this.userFinder = userFinder;
    }

    public List<UserGDPRTrainingDTO> getUsersWithGDPR() {
        return this.userFinder.getUsers()
                .stream()
                .map(user -> new UserGDPRTrainingDTO(new UserRestrictedDTO(user), false))

                .collect(Collectors.toList());
    }

    public void editGDPR(UserId userId, boolean gdprValue) throws UserNotFoundException {
        if(gdprValue) {
            this.repository.save(new UserGDPRTraining(userId));
        } else {
            this.repository.deleteById(userId);
        }
    }

}
