package it.chalmers.gamma.domain.user.service;

import it.chalmers.gamma.util.domain.Cid;
import it.chalmers.gamma.util.domain.Email;
import it.chalmers.gamma.util.domain.abstraction.EntityExists;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.util.domain.abstraction.GetAllEntities;
import it.chalmers.gamma.util.domain.abstraction.GetEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserFinder implements GetEntity<UserId, UserDTO>, GetAllEntities<UserRestrictedDTO>, EntityExists<UserId> {

    private final UserRepository userRepository;

    public UserFinder(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean exists(Cid cid) {
        return this.userRepository.existsByCid(cid);
    }

    public boolean exists(UserId id) {
        return this.userRepository.existsById(id);
    }

    public List<UserRestrictedDTO> getAll() {
        return getAllFull()
                .stream()
                .map(UserRestrictedDTO::new)
                .collect(Collectors.toList());
    }

    public UserDTO get(Email email) throws EntityNotFoundException {
        return getEntity(email).toDTO();
    }

    public UserDTO get(Cid cid) throws EntityNotFoundException {
        return getEntity(cid).toDTO();
    }

    public UserDTO get(UserId id) throws EntityNotFoundException {
        return getEntity(id).toDTO();
    }

    protected User getEntity(Cid cid) throws EntityNotFoundException {
        return this.userRepository.findByCid(cid)
                .orElseThrow(EntityNotFoundException::new);
    }

    protected User getEntity(Email email) throws EntityNotFoundException {
        return this.userRepository.findByEmail(email)
                .orElseThrow(EntityNotFoundException::new);
    }

    protected User getEntity(UserId id) throws EntityNotFoundException {
        return this.userRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    protected User getEntity(UserDTO user) throws EntityNotFoundException {
        return getEntity(user.id());
    }

    private List<UserDTO> getAllFull() {
        return userRepository.findAll().stream().map(User::toDTO).collect(Collectors.toList());
    }
}
