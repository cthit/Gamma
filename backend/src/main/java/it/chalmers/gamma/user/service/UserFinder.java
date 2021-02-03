package it.chalmers.gamma.user.service;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.Email;
import it.chalmers.gamma.user.data.User;
import it.chalmers.gamma.user.data.UserRepository;
import it.chalmers.gamma.user.dto.UserDTO;
import it.chalmers.gamma.user.exception.UserNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.time.Year;
import java.util.UUID;

@Component
public class UserFinder {

    private final UserRepository userRepository;

    public UserFinder(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean userExists(Cid cid) {
        return this.userRepository.existsByCid(cid.value);
    }

    public boolean userExists(UUID id) {
        return this.userRepository.existsById(id);
    }

    public UserDTO getUser(UserDTO userDTO) throws UserNotFoundException {
        return getUser(userDTO.getId());
    }

    public UserDTO getUser(Email email) throws UserNotFoundException {
        return this.toDTO(getUserEntity(email));
    }

    public UserDTO getUser(Cid cid) throws UserNotFoundException {
        return this.toDTO(getUserEntity(cid));
    }

    public UserDTO getUser(UUID id) throws UserNotFoundException {
        return this.toDTO(getUserEntity(id));
    }

    protected User getUserEntity(Cid cid) throws UserNotFoundException {
        return this.userRepository.findByCid(cid.value)
                .orElseThrow(UserNotFoundException::new);
    }

    protected User getUserEntity(Email email) throws UserNotFoundException {
        return this.userRepository.findByEmail(email.value)
                .orElseThrow(UserNotFoundException::new);
    }

    protected User getUserEntity(UUID id) throws UserNotFoundException {
        return this.userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }

    protected User getUserEntity(UserDTO user) throws UserNotFoundException {
        return getUserEntity(user.getId());
    }

    protected UserDTO toDTO(User user) {
        return new UserDTO.UserDTOBuilder()
                .phone(user.getPhone())
                .acceptanceYear(Year.of(user.getAcceptanceYear()))
                .accountLocked(user.isAccountLocked())
                .activated(user.isActivated())
                .avatarUrl(user.getAvatarUrl())
                .cid(new Cid(user.getCid()))
                .email(new Email(user.getEmail()))
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .gdpr(user.isGdpr())
                .id(user.getId())
                .nick(user.getNick())
                .userAgreement(user.isUserAgreement())
                .language(user.getLanguage())
                .build();
    }

}
