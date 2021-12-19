package it.chalmers.gamma.adapter.secondary.jpa.util;

import it.chalmers.gamma.app.common.Id;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

@MappedSuperclass
public abstract class MutableEntity<I extends Id<?>> extends AbstractEntity<I> {

    @Version
    @Column(name = "version")
    private int version;

    //TODO: Better exception
    public void throwIfNotValidVersion(int version) {
        if (this.version != version) {
            throw new StaleDomainObjectException();
        }
    }

    public int getVersion() {
        return this.version;
    }

    public static class StaleDomainObjectException extends RuntimeException { }
}
