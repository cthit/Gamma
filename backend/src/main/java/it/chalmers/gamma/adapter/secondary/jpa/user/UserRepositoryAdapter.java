package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.app.repository.UserRepository;
import it.chalmers.gamma.app.domain.common.Email;
import it.chalmers.gamma.app.domain.user.Cid;
import it.chalmers.gamma.app.domain.user.User;
import it.chalmers.gamma.app.domain.user.UserId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository repository;
    private final UserEntityConverter converter;

    public UserRepositoryAdapter(UserJpaRepository repository, UserEntityConverter converter) {
        this.repository = repository;
        this.converter = converter;
    }

    @Override
    public void save(User user) {
        this.repository.save(this.converter.toEntity(user));
    }

    @Override
    public void delete(UserId userId) throws UserNotFoundException {
        this.repository.deleteById(userId.value());
    }

    @Override
    public List<User> getAll() {
        return this.repository.findAll().stream().map(this.converter::toDomain).toList();
    }

    @Override
    public Optional<User> get(UserId userId) {
        return this.repository.findById(userId.getValue()).map(this.converter::toDomain);
    }

    @Override
    public Optional<User> get(Cid cid) {
        return this.repository.findByCid(cid.getValue()).map(this.converter::toDomain);
    }

    @Override
    public Optional<User> get(Email email) {
        return this.repository.findByEmail(email.value()).map(this.converter::toDomain);
    }
}
