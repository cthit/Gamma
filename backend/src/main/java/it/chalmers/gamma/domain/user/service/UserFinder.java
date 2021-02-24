package it.chalmers.gamma.domain.user.service;

import it.chalmers.gamma.domain.*;
import it.chalmers.gamma.domain.user.UserId;
import it.chalmers.gamma.domain.user.data.User;
import it.chalmers.gamma.domain.user.data.UserRepository;
import it.chalmers.gamma.domain.user.data.UserDTO;
import it.chalmers.gamma.domain.user.data.UserRestrictedDTO;
import org.springframework.stereotype.Component;

import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserFinder implements GetEntity<UserId, UserDTO>, GetAllEntities<UserDTO>, EntityExists<UserId> {

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

    public List<UserDTO> getAll() {
        return userRepository.findAll().stream().map(User::toDTO).collect(Collectors.toList());
    }

    public List<UserRestrictedDTO> getUsersRestricted() {
        return getAll()
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
        return getEntity(user.getId());
    }

}
