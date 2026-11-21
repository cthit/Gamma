package it.chalmers.gamma.adapter.secondary.jpa.util;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;
import org.springframework.data.domain.Persistable;

@MappedSuperclass
public abstract class AbstractEntity<ID> implements Persistable<ID> {

  @Column(name = "created_at", updatable = false, nullable = false)
  private Instant createdAt;

  @Transient private boolean persisted = false;

  @PrePersist
  protected void onCreate() {
    if (createdAt == null) {
      createdAt = Instant.now();
    }
  }

  @PostPersist
  @PostLoad
  void setPersisted() {
    persisted = true;
  }

  protected Instant getCreatedAt() {
    return createdAt;
  }

  protected void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  @Override
  public boolean isNew() {
    return !persisted;
  }

  @Override
  public final int hashCode() {
    assert (getId() != null);

    return Objects.hash(getId().hashCode());
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    return Objects.equals(this.getId(), ((AbstractEntity<?>) o).getId());
  }
}
