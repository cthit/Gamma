package it.chalmers.gamma.adapter.secondary.jpa.util;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;

@MappedSuperclass
public abstract class MutableEntity<ID> extends AbstractEntity<ID> {

  /**
   * It is the responsibility of each entity converter to manage the version. Not
   * using @jakarta.persistence.Version since it doesn't handle foreign keys such as members for a
   * group.
   */
  @Column(name = "version")
  private int version;

  @Column(name = "updated_at")
  private Instant updatedAt;

  @Override
  protected void onCreate() {
    super.onCreate();
    updatedAt = Instant.now();
  }

  protected MutableEntity() {
    this.version = 0;
  }

  /**
   * If not the correct version is provided, then the data that is being tried to be converted is
   * outdated.
   */
  public void increaseVersion(int currentVersion) {
    /*
     * If id is null, then currentVersion must be 0. This to indicate that the incoming entity is new.
     * If not, then something is trying to save an entity that has been deleted.
     */
    if (this.getId() == null && currentVersion != 0) {
      throw new IllegalEntityStateException();
    }
    // Version has to match the current version.
    else if (this.version != currentVersion) {
      throw new StaleDomainObjectException();
    }

    // Checks passed, updating the version.
    this.version++;
    this.updatedAt = Instant.now();
  }

  public int getVersion() {
    return this.version;
  }

  public static class IllegalEntityStateException extends RuntimeException {}

  public static class StaleDomainObjectException extends RuntimeException {}
}
