package it.chalmers.gamma.user;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.Email;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

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

    public UserDTO getUser(UserDTO userDTO) {
        return getUser(userDTO.getId());
    }

    public UserDTO getUser(Email email) {
        return getUserEntity(email).toDTO();
    }

    public UserDTO getUser(Cid cid) {
        return getUserEntity(cid).toDTO();
    }

    public UserDTO getUser(UUID id) {
        return getUserEntity(id).toDTO();
    }

    protected User getUserEntity(Cid cid) {
        return this.userRepository.findByCid(cid.value)
                .orElseThrow(() -> new UsernameNotFoundException("User could not be found"));
    }

    protected User getUserEntity(Email email) {
        return this.userRepository.findByEmail(email.value)
                .orElseThrow(() -> new UsernameNotFoundException("User could not be found"));
    }

    protected User getUserEntity(UUID id) {
        return this.userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User could not be found"));
    }

    protected User getUserEntity(UserDTO user) {
        return getUserEntity(user.getId());
    }

}
