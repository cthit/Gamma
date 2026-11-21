package it.chalmers.gamma.adapter.secondary.jpa.user.admin;

import it.chalmers.gamma.app.admin.domain.AdminRepository;
import it.chalmers.gamma.app.signin.domain.RecoveryKeyRepository;
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

  private final RecoveryKeyRepository recoveryKeyRepository;

  public AdminEntityRepositoryAdapter(
      AdminEntityJpaRepository repository,
      UserRepository userRepository,
      RecoveryKeyRepository recoveryKeyRepository) {
    this.userRepository = userRepository;
    this.repository = repository;
    this.recoveryKeyRepository = recoveryKeyRepository;
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
        // Admins never use recovery keys; drop any old key so it can't reactivate on demotion.
        this.recoveryKeyRepository.delete(userId);
      }
    }
  }

  @Override
  public List<UserId> getAll() {
    return this.repository.findAll().stream()
        .map(adminEntity -> new UserId(adminEntity.getId()))
        .toList();
  }

  @Override
  public void lockAdminChanges() {
    this.repository.lockAdminChanges();
  }

  @Override
  public long countUsableAdmins() {
    this.repository.flush();
    return this.repository.countUsableAdmins();
  }

  @Override
  public long countUsableAdminsOtherThan(UserId userId) {
    this.repository.flush();
    return this.repository.countUsableAdminsOtherThan(userId.value());
  }
}
