package it.chalmers.gamma.app.admin;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.chalmers.gamma.app.admin.domain.AdminRepository;
import it.chalmers.gamma.app.user.domain.UserId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class UsableAdminGuardTest {

  private final FakeAdmins admins = new FakeAdmins();
  private final UsableAdminGuard guard = new UsableAdminGuard(this.admins);
  private final UserId target = new UserId(UUID.randomUUID());

  @Test
  void refusesAChangeThatLeavesNoUsableAdmin() {
    this.admins.usable = 1;
    UsableAdminGuard.Check check = this.guard.lock();
    this.admins.usable = 0;

    assertThrows(
        UsableAdminGuard.NoUsableAdminLeftException.class, check::assertUsableAdminRemains);
  }

  @Test
  void allowsAChangeThatKeepsAUsableAdmin() {
    this.admins.usable = 2;
    UsableAdminGuard.Check check = this.guard.lock();
    this.admins.usable = 1;

    assertDoesNotThrow(check::assertUsableAdminRemains);
  }

  @Test
  void refusesToDeleteTheLastUsableAdminBeforeDeletingAnything() {
    this.admins.usable = 1;
    this.admins.usableOtherThanTarget = 0;
    UsableAdminGuard.Check check = this.guard.lock();

    assertThrows(
        UsableAdminGuard.NoUsableAdminLeftException.class,
        () -> check.assertUsableAdminRemainsWithout(this.target));
    this.admins.usableOtherThanTarget = 1;
    assertDoesNotThrow(() -> check.assertUsableAdminRemainsWithout(this.target));
  }

  @Test
  void allowsChangesBeforeTheFirstAdminHasAPasskey() {
    this.admins.usable = 0;
    UsableAdminGuard.Check check = this.guard.lock();

    assertDoesNotThrow(check::assertUsableAdminRemains);
    assertDoesNotThrow(() -> check.assertUsableAdminRemainsWithout(this.target));
  }

  @Test
  void locksBeforeCountingTheUsableAdmins() {
    this.guard.lock();

    assertEquals(List.of("lock", "count"), this.admins.calls);
  }

  private static final class FakeAdmins implements AdminRepository {
    private final List<String> calls = new ArrayList<>();
    private long usable;
    private long usableOtherThanTarget;

    @Override
    public void lockAdminChanges() {
      this.calls.add("lock");
    }

    @Override
    public long countUsableAdmins() {
      this.calls.add("count");
      return this.usable;
    }

    @Override
    public long countUsableAdminsOtherThan(UserId userId) {
      return this.usableOtherThanTarget;
    }

    @Override
    public boolean isAdmin(UserId userId) {
      throw new AssertionError();
    }

    @Override
    public void setAdmin(UserId userId, boolean admin) {
      throw new AssertionError();
    }

    @Override
    public List<UserId> getAll() {
      throw new AssertionError();
    }
  }
}
