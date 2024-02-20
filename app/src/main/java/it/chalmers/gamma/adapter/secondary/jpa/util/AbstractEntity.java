package it.chalmers.gamma.adapter.secondary.jpa.util;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Transient;
import java.util.Objects;
import org.springframework.data.domain.Persistable;

@MappedSuperclass
public abstract class AbstractEntity<ID> implements Persistable<ID> {

  @Transient private boolean persisted = false;

  @PostPersist
  @PostLoad
  void setPersisted() {
    persisted = true;
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
