package it.chalmers.gamma.internal.user.service;

import it.chalmers.gamma.domain.Email;
import it.chalmers.gamma.domain.UnencryptedPassword;
import it.chalmers.gamma.domain.User;
import it.chalmers.gamma.domain.UserId;
import it.chalmers.gamma.domain.Cid;

import it.chalmers.gamma.domain.UserRestricted;
import it.chalmers.gamma.file.response.InvalidFileTypeResponse;
import it.chalmers.gamma.util.ImageUtils;

import java.util.List;
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

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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


    public void editProfilePicture(User user, MultipartFile file) throws UserNotFoundException {
        if (ImageUtils.isImageOrGif(file)) {
//            try {
//                if (!user.avatarUrl().equals("default.jpg")) {
//                    ImageUtils.removeImage(user.avatarUrl());
//                }
//                String fileUrl = ImageUtils.saveImage(file, user.cid().get());
//                UserDTO newUser = new UserDTO(
//                        user.id(),
//                        user.cid(),
//                        user.email(),
//                        user.language(),
//                        user.nick(),
//                        user.firstName(),
//                        user.lastName(),
//                        fileUrl,
//                        user.userAgreement(),
//                        user.acceptanceYear(),
//                        user.activated()
//                );
//
//                update(newUser);
//            } catch (FileNotFoundResponse e) {
//                throw new FileNotSavedResponse();
//            }
        } else {
            throw new InvalidFileTypeResponse();
        }
    }

    public boolean passwordMatches(UserId userId, String password) throws UserNotFoundException {
        UserEntity user = this.getEntity(userId);
        return this.passwordEncoder.matches(password, user.getPassword().get());
    }

    protected UserEntity getEntity(UserId id) throws UserNotFoundException {
        return this.userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }

    public static class UserNotFoundException extends Exception {
    }

}
