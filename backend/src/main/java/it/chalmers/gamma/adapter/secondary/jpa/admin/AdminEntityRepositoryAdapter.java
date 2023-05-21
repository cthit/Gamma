package it.chalmers.gamma.adapter.secondary.jpa.admin;

import it.chalmers.gamma.app.admin.domain.AdminRepository;
import it.chalmers.gamma.app.user.domain.UserId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AdminEntityRepositoryAdapter implements AdminRepository {

    private final AdminEntityJpaRepository repository;

    public AdminEntityRepositoryAdapter(AdminEntityJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean isAdmin(UserId userId) {
        return this.repository.findById(userId.value()).isPresent();
    }

    @Override
    public void setAdmin(UserId userId, boolean admin) {
        Optional<AdminEntity> maybeAdminEntity = this.repository.findById(userId.value());

        if (maybeAdminEntity.isPresent()) {
            if(!admin) {
                this.repository.deleteById(userId.value());
            }
        } else {
            if(admin) {
                this.repository.save(new AdminEntity(userId.value()));
            }
        }
    }

    @Override
    public List<UserId> getAll() {
        return this.repository
                .findAll()
                .stream()
                .map(adminEntity -> new UserId(adminEntity.getId()))
                .toList();
    }

}
