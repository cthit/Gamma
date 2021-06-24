package it.chalmers.gamma.app.service;

import it.chalmers.gamma.app.domain.UserId;
import org.springframework.stereotype.Service;

@Service
public class UserLockedService {

    private final UserLockedRepository repository;

    public UserLockedService(UserLockedRepository repository) {
        this.repository = repository;
    }

    public void lockUser(UserId userId) {
        this.repository.save(new UserLockedEntity(userId));
    }

    public boolean isLocked(UserId userId) {
        return this.repository.findById(userId).isPresent();
    }

}
