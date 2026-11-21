package it.chalmers.gamma.app.admin.domain;

import it.chalmers.gamma.app.user.domain.UserId;
import java.util.List;

public interface AdminRepository {

  boolean isAdmin(UserId userId);

  void setAdmin(UserId userId, boolean admin);

  List<UserId> getAll();

  /** Waits for other changes that could leave no usable admin, until the transaction ends. */
  void lockAdminChanges();

  /** Admins who aren't locked and have at least one passkey. */
  long countUsableAdmins();

  long countUsableAdminsOtherThan(UserId userId);
}
