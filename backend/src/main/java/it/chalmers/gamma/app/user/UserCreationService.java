package it.chalmers.gamma.app.user;

import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntity;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserJpaRepository;
import it.chalmers.gamma.app.domain.UserActivationToken;
import it.chalmers.gamma.app.domain.UnencryptedPassword;
import it.chalmers.gamma.app.domain.User;
import it.chalmers.gamma.app.whitelist.WhitelistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserCreationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserCreationService.class);

    private final WhitelistService whitelistService;
    private final UserActivationService userActivationService;
    private final PasswordEncoder passwordEncoder;
    private final UserJpaRepository repository;

    public UserCreationService(WhitelistService whitelistService,
                               UserActivationService userActivationService,
                               PasswordEncoder passwordEncoder,
                               UserJpaRepository repository) {
        this.whitelistService = whitelistService;
        this.userActivationService = userActivationService;
        this.passwordEncoder = passwordEncoder;
        this.repository = repository;
    }

    public void createUserByCode(User newUser, UnencryptedPassword password, UserActivationToken token) throws CidOrCodeNotMatchException {
        if(!userActivationService.codeMatchesCid(newUser.cid(), token)) {
            throw new CidOrCodeNotMatchException();
        }

        //TODO: Check if code is still valid.

        try {
            this.whitelistService.delete(newUser.cid());
            this.userActivationService.delete(newUser.cid());

            this.createUser(newUser, password);
        } catch (UserActivationService.UserActivationNotFoundException | WhitelistService.WhitelistNotFoundException e) {
            LOGGER.error("Something went wrong when clearing whitelist and/or activation code", e);

            //Throwing generic error for security reasons
            throw new CidOrCodeNotMatchException();
        }

    }

    //TODO throw exception if something goes wrong e.g. same type
    //I will because of this fix things in HaveUserThatIsAdminBootstrap
    public void createUser(User newUser, UnencryptedPassword password) {
        UserEntity user = new UserEntity(newUser);
        user.setPassword(password.encrypt(this.passwordEncoder));

        this.repository.save(user);
    }

}
