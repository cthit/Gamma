package it.chalmers.gamma.app.admin.domain;

import it.chalmers.gamma.app.user.domain.UserId;
import java.util.List;

public interface AdminRepository {

  boolean isAdmin(UserId userId);

  void setAdmin(UserId userId, boolean admin);

  List<UserId> getAll();
}
