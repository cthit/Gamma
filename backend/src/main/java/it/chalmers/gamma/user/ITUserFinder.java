package it.chalmers.gamma.user;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.Email;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ITUserFinder {

    private final ITUserRepository userRepository;

    public ITUserFinder(ITUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean userExists(Cid cid) {
        return this.userRepository.existsByCid(cid.value);
    }

    public boolean userExists(UUID id) {
        return this.userRepository.existsById(id);
    }

    public ITUserDTO getUser(ITUserDTO userDTO) {
        return getUser(userDTO.getId());
    }

    public ITUserDTO getUser(Email email) {
        return getUserEntity(email).toDTO();
    }

    public ITUserDTO getUser(Cid cid) {
        return getUserEntity(cid).toDTO();
    }

    public ITUserDTO getUser(UUID id) {
        return getUserEntity(id).toDTO();
    }

    protected ITUser getUserEntity(Cid cid) {
        return this.userRepository.findByCid(cid.value)
                .orElseThrow(() -> new UsernameNotFoundException("User could not be found"));
    }

    protected ITUser getUserEntity(Email email) {
        return this.userRepository.findByEmail(email.value)
                .orElseThrow(() -> new UsernameNotFoundException("User could not be found"));
    }

    protected ITUser getUserEntity(UUID id) {
        return this.userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User could not be found"));
    }

    protected ITUser getUserEntity(ITUserDTO user) {
        return getUserEntity(user.getId());
    }

}
