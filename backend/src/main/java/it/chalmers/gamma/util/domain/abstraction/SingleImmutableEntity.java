package it.chalmers.gamma.util.domain.abstraction;

import javax.persistence.MappedSuperclass;
import java.util.Objects;

@MappedSuperclass
public abstract class SingleImmutableEntity<S extends Id> {

    protected abstract S get();

    @Override
    public final int hashCode() {
        assert(get() != null);

        return Objects.hash(get().hashCode());
    }

    @Override
    public final boolean equals(Object obj) {
        assert(get() != null);

        return super.equals(obj);
    }
}
