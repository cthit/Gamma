package it.chalmers.gamma.adapter.secondary.jpa.util;

import it.chalmers.gamma.app.common.Id;
import org.springframework.data.domain.Persistable;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Objects;

@MappedSuperclass
public abstract class AbstractEntity<I extends Id<?>> implements Serializable, Persistable<I> {

    @Override
    public boolean isNew() {
        return true;
    }

    @Override
    public final int hashCode() {
        assert(getId() != null);

        return Objects.hash(getId().hashCode());
    }

    @Override
    public final boolean equals(Object o) {
        assert(getId() != null);

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        return this.getId().equals(((AbstractEntity<?>) o).getId());
    }
}
