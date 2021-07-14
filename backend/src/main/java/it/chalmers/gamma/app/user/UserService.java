package it.chalmers.gamma.app.user;

import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntity;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserJpaRepository;
import it.chalmers.gamma.app.domain.Email;
import it.chalmers.gamma.app.domain.UnencryptedPassword;
import it.chalmers.gamma.app.domain.User;
import it.chalmers.gamma.app.domain.UserId;
import it.chalmers.gamma.app.domain.Cid;

import it.chalmers.gamma.util.component.ImageService;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserAvatarService userAvatarService;
    private final ImageService imageService;

    public UserService(UserJpaRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       UserAvatarService userAvatarService,
                       ImageService imageService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userAvatarService = userAvatarService;
        this.imageService = imageService;
    }

    public void delete(UserId id) {
        this.userRepository.deleteById(id);
    }

    public void update(User newEdit) throws UserNotFoundException {
        UserEntity user = this.getEntity(newEdit.id());
        user.apply(newEdit);
        this.userRepository.save(user);
    }

    //todo filter out non activated accounts
    public List<User> getAll() {
        return this.userRepository.findAll()
                .stream()
                .map(UserEntity::toDomain)
                .collect(Collectors.toList());
    }

    public User get(Email email) throws UserNotFoundException {
        return this.userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new)
                .toDomain();
    }

    public User get(Cid cid) throws UserNotFoundException {
        return this.userRepository.findByCid(cid)
                .orElseThrow(UserNotFoundException::new)
                .toDomain();
    }

    public User get(UserId id) throws UserNotFoundException {
        return this.userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new)
                .toDomain();
    }

    public void setPassword(UserId userId, UnencryptedPassword password) throws UserNotFoundException {
        UserEntity user = this.getEntity(userId);
        user.setPassword(password.encrypt(this.passwordEncoder));
        this.userRepository.save(user);
    }

    protected UserEntity getEntity(UserId id) throws UserNotFoundException {
        return this.userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }

    public void resetAllUserAgreements() {
        List<UserEntity> userEntities = this.userRepository.findAll();
        userEntities.forEach(UserEntity::resetUserAgreement);
        this.userRepository.saveAll(userEntities);
    }

    public static class UserNotFoundException extends Exception { }

}
