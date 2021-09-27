package it.chalmers.gamma.adapter.secondary.jpa.util;

import it.chalmers.gamma.app.domain.Id;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

@MappedSuperclass
public abstract class MutableEntity<I extends Id<?>> extends AbstractEntity<I> {

    @Version
    @Column(name = "version")
    private int version;

}
