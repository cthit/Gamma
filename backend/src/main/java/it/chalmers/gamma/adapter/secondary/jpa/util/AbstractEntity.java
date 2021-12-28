package it.chalmers.gamma.adapter.secondary.jpa.util;

import org.springframework.data.domain.Persistable;

import javax.persistence.MappedSuperclass;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.Transient;
import java.util.Objects;

@MappedSuperclass
public abstract class AbstractEntity<ID> implements Persistable<ID> {

    @Transient
    private boolean persisted = false;

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
        assert(getId() != null);

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
