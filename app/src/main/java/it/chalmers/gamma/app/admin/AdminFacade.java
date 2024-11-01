package it.chalmers.gamma.app.admin;

import static it.chalmers.gamma.app.authentication.AccessGuard.isAdmin;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.admin.domain.AdminRepository;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.user.domain.UserId;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class AdminFacade extends Facade {

  private final AdminRepository adminRepository;

  public AdminFacade(AccessGuard accessGuard, AdminRepository adminRepository) {
    super(accessGuard);
    this.adminRepository = adminRepository;
  }

  public void updateAdmins(List<UUID> newAdmins, List<UUID> adminsToRemove) {
    accessGuard.require(isAdmin());

    if (newAdmins.isEmpty()) {
      throw new IllegalArgumentException("There must be at least one admin");
    }

    for (UUID userId : newAdmins) {
      this.adminRepository.setAdmin(new UserId(userId), true);
    }

    for (UUID userId : adminsToRemove) {
      this.adminRepository.setAdmin(new UserId(userId), false);
    }
  }

  public List<UUID> getAllAdmins() {
    accessGuard.require(isAdmin());

    return this.adminRepository.getAll().stream().map(UserId::value).toList();
  }
}
