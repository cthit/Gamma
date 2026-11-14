package it.chalmers.gamma.adapter.secondary.jpa.user.admin;

import it.chalmers.gamma.app.admin.domain.AdminRepository;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class AdminEntityRepositoryAdapter implements AdminRepository {

  private final AdminEntityJpaRepository repository;

  private final UserRepository userRepository;

  public AdminEntityRepositoryAdapter(
      AdminEntityJpaRepository repository, UserRepository userRepository) {
    this.userRepository = userRepository;
    this.repository = repository;
  }

  @Override
  public boolean isAdmin(UserId userId) {
    return this.repository.findById(userId.value()).isPresent();
  }

  @Override
  @Transactional
  public void setAdmin(UserId userId, boolean admin) {
    this.userRepository.lockSignInMethods(userId);
    Optional<AdminEntity> maybeAdminEntity = this.repository.findById(userId.value());

    if (maybeAdminEntity.isPresent()) {
      if (!admin) {
        this.repository.deleteById(userId.value());
      }
    } else {
      if (admin) {
        this.repository.save(new AdminEntity(userId.value()));
      }
    }
  }

  @Override
  public List<UserId> getAll() {
    return this.repository.findAll().stream()
        .map(adminEntity -> new UserId(adminEntity.getId()))
        .toList();
  }
}
