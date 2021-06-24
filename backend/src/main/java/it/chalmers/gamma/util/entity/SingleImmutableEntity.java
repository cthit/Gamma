package it.chalmers.gamma.util.entity;

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
