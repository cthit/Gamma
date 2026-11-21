package it.chalmers.gamma.app.admin;

import it.chalmers.gamma.app.admin.domain.AdminRepository;
import it.chalmers.gamma.app.user.domain.UserId;
import org.springframework.stereotype.Service;

/**
 * Keeps at least one admin able to sign in: an admin who isn't locked and has a passkey. Admins
 * can't use email or admin recovery, so without one nobody could administer Gamma again.
 */
@Service
public class UsableAdminGuard {

  private final AdminRepository adminRepository;

  public UsableAdminGuard(AdminRepository adminRepository) {
    this.adminRepository = adminRepository;
  }

  /**
   * Call inside the transaction of the change, before it locks any user, then check the returned
   * {@link Check}. Changes guarded this way run one at a time.
   */
  public Check lock() {
    this.adminRepository.lockAdminChanges();
    return new Check(this.adminRepository.countUsableAdmins() > 0);
  }

  public final class Check {

    // A fresh install starts with an admin who hasn't added a passkey yet.
    private final boolean hadUsableAdmin;

    private Check(boolean hadUsableAdmin) {
      this.hadUsableAdmin = hadUsableAdmin;
    }

    public void assertUsableAdminRemains() {
      if (this.hadUsableAdmin && adminRepository.countUsableAdmins() == 0) {
        throw new NoUsableAdminLeftException();
      }
    }

    /**
     * Checks before deleting the user, because deleting also revokes sessions and authorizations
     * that a rollback can't restore.
     */
    public void assertUsableAdminRemainsWithout(UserId userId) {
      if (this.hadUsableAdmin && adminRepository.countUsableAdminsOtherThan(userId) == 0) {
        throw new NoUsableAdminLeftException();
      }
    }
  }

  /** Unchecked, so that the transaction of the refused change is rolled back. */
  public static final class NoUsableAdminLeftException extends RuntimeException {}
}
