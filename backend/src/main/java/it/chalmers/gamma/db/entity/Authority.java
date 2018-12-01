package it.chalmers.gamma.db.entity;

import it.chalmers.gamma.db.entity.pk.AuthorityPK;

import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Target;

@Entity
@Table(name = "authority")
public class Authority {

    @Target(AuthorityPK.class)
    @EmbeddedId
    AuthorityPK id;

    @Column(name = "id")
    private UUID internalId;

    @JoinColumn(name = "authority_level")
    @OneToOne
    private AuthorityLevel authorityLevel;

    public AuthorityPK getId() {
        return this.id;
    }

    public void setId(AuthorityPK id) {
        this.id = id;
    }

    public AuthorityLevel getAuthorityLevel() {
        return this.authorityLevel;
    }

    public void setAuthorityLevel(AuthorityLevel authorityLevel) {
        this.authorityLevel = authorityLevel;
    }

    public UUID getInternalID() {
        return this.internalId;
    }

    public void setInternalID(UUID internalID) {
        this.internalId = internalID;
    }

    public Authority() {
        this.internalId = UUID.randomUUID();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Authority authority = (Authority) o;
        return Objects.equals(this.id, authority.id)
            && Objects.equals(this.authorityLevel, authority.authorityLevel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.authorityLevel);
    }

    @Override
    public String toString() {
        return "Authority{"
            + "id=" + this.id
            + ", authorityLevel=" + this.authorityLevel
            + '}';
    }
}
