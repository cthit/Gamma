package it.chalmers.gamma.adapter.secondary.jpa.util;

import it.chalmers.gamma.app.domain.Id;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Objects;

@MappedSuperclass
public abstract class AbstractEntity<I extends Id<?>> implements Serializable {

    protected abstract I domainId();

    @Override
    public final int hashCode() {
        assert(domainId() != null);

        return Objects.hash(domainId().hashCode());
    }

    @Override
    public final boolean equals(Object o) {
        assert(domainId() != null);

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        return this.domainId().equals(((AbstractEntity<?>) o).domainId());
    }
}
