package it.chalmers.gamma.util.domain.abstraction;

import java.io.Serializable;
import java.util.Objects;

public abstract class Id<S> implements Serializable {

    protected abstract S get();

    public final int hashCode() {
        assert(get() != null);

        return Objects.hash(get());
    }

    public final boolean equals(Object o) {
        assert(get() != null);

        if (o instanceof Id) {
            return this.get().equals(((Id<?>) o).get());
        }

        return false;
    }

    public final String toString() {
        return "Id: " + get();
    }

}
