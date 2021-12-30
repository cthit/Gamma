package it.chalmers.gamma.adapter.secondary.jpa.util;


import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class MutableEntity<ID> extends AbstractEntity<ID> {

    protected MutableEntity() {
        this.version = 0;
    }

    /**
     * It is the responsibility of each entity converter to manage the version.
     * Not using @javax.persistence.Version since it doesn't handle foreign keys
     * such as members for a group.
     */
    @Column(name = "version")
    private int version;

    /**
     * If not the correct version is provided, then the data
     * that is being tried to be converted is outdated.
     */
    public void increaseVersion(int currentVersion) {
        if (this.version != currentVersion) {
            throw new StaleDomainObjectException();
        }

        this.version++;
    }

    public int getVersion() {
        return this.version;
    }

    public static class StaleDomainObjectException extends RuntimeException { }
}
