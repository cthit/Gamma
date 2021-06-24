package it.chalmers.gamma.internal.user.service;

import it.chalmers.gamma.domain.Email;
import it.chalmers.gamma.domain.ImageUri;
import it.chalmers.gamma.domain.UnencryptedPassword;
import it.chalmers.gamma.domain.User;
import it.chalmers.gamma.domain.UserAvatar;
import it.chalmers.gamma.domain.UserId;
import it.chalmers.gamma.domain.Cid;

import it.chalmers.gamma.domain.UserRestricted;
import it.chalmers.gamma.internal.useravatar.service.UserAvatarService;
import it.chalmers.gamma.util.UserUtils;
import it.chalmers.gamma.util.component.ImageService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserAvatarService userAvatarService;
    private final ImageService imageService;

    public UserService(UserRepository userRepository,
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
    public List<UserRestricted> getAll() {
        return this.userRepository.findAll()
                .stream()
                .map(UserEntity::toDTO)
                .map(UserRestricted::new)
                .collect(Collectors.toList());
    }

    public User get(Email email) throws UserNotFoundException {
        return this.userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new)
                .toDTO();
    }

    public User get(Cid cid) throws UserNotFoundException {
        return this.userRepository.findByCid(cid)
                .orElseThrow(UserNotFoundException::new)
                .toDTO();
    }

    public User get(UserId id) throws UserNotFoundException {
        return this.userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new)
                .toDTO();
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
