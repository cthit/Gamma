package it.chalmers.gamma.domain.user.service;

import it.chalmers.gamma.domain.EntityNotFoundException;
import it.chalmers.gamma.domain.activationcode.service.ActivationCodeFinder;
import it.chalmers.gamma.domain.activationcode.service.ActivationCodeService;
import it.chalmers.gamma.domain.user.data.User;
import it.chalmers.gamma.domain.user.data.UserRepository;
import it.chalmers.gamma.domain.user.data.UserDTO;
import it.chalmers.gamma.domain.user.exception.CidOrCodeNotMatchException;
import it.chalmers.gamma.domain.whitelist.exception.CidNotWhitelistedException;
import it.chalmers.gamma.domain.whitelist.service.WhitelistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserCreationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserCreationService.class);

    private final WhitelistService whitelistService;
    private final ActivationCodeService activationCodeService;
    private final ActivationCodeFinder activationCodeFinder;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;

    public UserCreationService(WhitelistService whitelistService,
                               ActivationCodeService activationCodeService,
                               ActivationCodeFinder activationCodeFinder,
                               PasswordEncoder passwordEncoder,
                               UserRepository repository) {
        this.whitelistService = whitelistService;
        this.activationCodeService = activationCodeService;
        this.activationCodeFinder = activationCodeFinder;
        this.passwordEncoder = passwordEncoder;
        this.repository = repository;
    }

    public void createUserByCode(UserDTO newUser, String password, String code) throws CidOrCodeNotMatchException {
        if(!activationCodeFinder.codeMatchesCid(newUser.getCid(), code)) {
            throw new CidOrCodeNotMatchException();
        }

        try {
            this.whitelistService.delete(newUser.getCid());
            this.activationCodeService.delete(newUser.getCid());

            this.createUser(newUser, password);
        } catch (EntityNotFoundException e) {
            LOGGER.error("Something went wrong when clearing whitelist and/or activation code", e);

            //Throwing generic error for security reasons
            throw new CidOrCodeNotMatchException();
        }

    }

    public void createUser(UserDTO newUser, String password) {
        User user = new User(newUser);
        user.setPassword(this.passwordEncoder.encode(password));

        this.repository.save(user);
    }

}
