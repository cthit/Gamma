package it.chalmers.gamma.app.admin;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.admin.domain.AdminRepository;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.user.domain.UserId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static it.chalmers.gamma.app.authentication.AccessGuard.isAdmin;

@Component
public class AdminFacade extends Facade {

    private final AdminRepository adminRepository;

    public AdminFacade(AccessGuard accessGuard, AdminRepository adminRepository) {
        super(accessGuard);
        this.adminRepository = adminRepository;
    }

    public void setAdmin(UUID userId, boolean admin) {
        super.accessGuard.require(isAdmin());

        this.adminRepository.setAdmin(new UserId(userId), admin);
    }

    public List<UUID> getAllAdmins() {
        return this.adminRepository
                .getAll()
                .stream()
                .map(UserId::value)
                .toList();
    }

}
