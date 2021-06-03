package it.chalmers.gamma.internal.user.service;

import it.chalmers.gamma.domain.Code;
import it.chalmers.gamma.domain.UnencryptedPassword;
import it.chalmers.gamma.internal.activationcode.service.ActivationCodeService;
import it.chalmers.gamma.internal.whitelist.service.WhitelistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserCreationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserCreationService.class);

    private final WhitelistService whitelistService;
    private final ActivationCodeService activationCodeService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;

    public UserCreationService(WhitelistService whitelistService,
                               ActivationCodeService activationCodeService,
                               PasswordEncoder passwordEncoder,
                               UserRepository repository) {
        this.whitelistService = whitelistService;
        this.activationCodeService = activationCodeService;
        this.passwordEncoder = passwordEncoder;
        this.repository = repository;
    }

    public void createUserByCode(UserDTO newUser, UnencryptedPassword password, Code code) throws CidOrCodeNotMatchException {
        if(!activationCodeService.codeMatchesCid(newUser.cid(), code)) {
            throw new CidOrCodeNotMatchException();
        }

        //TODO: Check if code is still valid.

        try {
            this.whitelistService.delete(newUser.cid());
            this.activationCodeService.delete(newUser.cid());

            this.createUser(newUser, password);
        } catch (ActivationCodeService.ActivationCodeNotFoundException | WhitelistService.WhitelistNotFoundException e) {
            LOGGER.error("Something went wrong when clearing whitelist and/or activation code", e);

            //Throwing generic error for security reasons
            throw new CidOrCodeNotMatchException();
        }

    }

    //TODO throw exception if something goes wrong e.g. same name
    //I will because of this fix things in HaveUserThatIsAdminBootstrap
    public void createUser(UserDTO newUser, UnencryptedPassword password) {
        UserEntity user = new UserEntity(newUser);
        user.setPassword(password.encrypt(this.passwordEncoder));

        this.repository.save(user);
    }

}
