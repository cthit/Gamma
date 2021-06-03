package it.chalmers.gamma.internal.user.service;

import it.chalmers.gamma.domain.UserId;
import org.springframework.stereotype.Service;

@Service
public class MeService {

    private final UserService userService;

    public MeService(UserService userService) {
        this.userService = userService;
    }

    public void tryToDeleteUser(UserId userId, String password) throws UserService.UserNotFoundException {
        if (this.userService.passwordMatches(userId, password)) {
            this.userService.delete(userId);
        }
    }

}
