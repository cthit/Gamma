package it.chalmers.gamma.adapter.secondary.jpa.util;

import it.chalmers.gamma.app.domain.Id;

import javax.persistence.MappedSuperclass;
import java.util.Objects;

@MappedSuperclass
public abstract class SingleImmutableEntity<S extends Id<?>> {

    protected abstract S get();

    @Override
    public int hashCode() {
        assert(get() != null);

        return Objects.hash(get().hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        assert(get() != null);

        return super.equals(obj);
    }
}
