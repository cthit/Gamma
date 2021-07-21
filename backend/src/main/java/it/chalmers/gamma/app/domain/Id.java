package it.chalmers.gamma.app.domain;

import java.io.Serializable;
import java.util.Objects;

public abstract class Id<S> implements Serializable {

    public abstract S value();

    public final int hashCode() {
        assert(value() != null);

        return Objects.hash(value());
    }

    public final boolean equals(Object o) {
        assert(value() != null);

        if (o instanceof Id) {
            return this.value().equals(((Id<?>) o).value());
        }

        return false;
    }

    public final String toString() {
        return this.value().toString();
    }

}
