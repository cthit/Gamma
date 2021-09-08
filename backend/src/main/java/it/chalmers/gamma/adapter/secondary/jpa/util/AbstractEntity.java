package it.chalmers.gamma.adapter.secondary.jpa.util;

import it.chalmers.gamma.domain.Id;

import javax.persistence.MappedSuperclass;
import java.util.Objects;

@MappedSuperclass
public abstract class AbstractEntity<I extends Id<?>> {

    protected abstract I id();

    @Override
    public final int hashCode() {
        assert(id() != null);

        return Objects.hash(id().hashCode());
    }

    @Override
    public final boolean equals(Object o) {
        assert(id() != null);

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        return this.id().equals(((AbstractEntity<?>) o).id());
    }
}
