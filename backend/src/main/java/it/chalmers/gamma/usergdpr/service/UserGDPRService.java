package it.chalmers.gamma.usergdpr.service;

import it.chalmers.gamma.domain.IDsNotMatchingException;
import it.chalmers.gamma.user.dto.UserDTO;
import it.chalmers.gamma.user.dto.UserRestrictedDTO;
import it.chalmers.gamma.user.exception.UserNotFoundException;
import it.chalmers.gamma.user.service.UserFinder;
import it.chalmers.gamma.user.service.UserService;
import it.chalmers.gamma.usergdpr.dto.UserWithGDPRDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserGDPRService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserGDPRService.class);

    private final UserService userService;
    private final UserFinder userFinder;

    public UserGDPRService(UserService userService, UserFinder userFinder) {
        this.userService = userService;
        this.userFinder = userFinder;
    }

    public List<UserWithGDPRDTO> getUsersWithGDPR() {
        return this.userFinder.getUsers()
                .stream()
                .map(user -> new UserWithGDPRDTO(new UserRestrictedDTO(user), user.isGdpr()))
                .collect(Collectors.toList());
    }

    public void editGDPR(UUID userId, boolean gdprValue) throws UserNotFoundException {
        UserDTO user = userFinder.getUser(userId);

        try {
            userService.editUser(
                    new UserDTO.UserDTOBuilder()
                            .from(user)
                            .gdpr(gdprValue)
                            .build()
            );
        } catch (IDsNotMatchingException e) {
            LOGGER.error("ERROR EDITING USER", e);
        }
    }

}
