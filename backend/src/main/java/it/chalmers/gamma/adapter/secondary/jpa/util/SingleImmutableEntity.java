package it.chalmers.gamma.adapter.secondary.jpa.util;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class SingleImmutableEntity<ID> extends AbstractEntity<ID> {

    protected abstract ID get();

    @Override
    public ID getId() {
        return get();
    }
}
