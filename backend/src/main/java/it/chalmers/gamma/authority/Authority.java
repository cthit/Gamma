package it.chalmers.gamma.authority;

import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Target;

@Entity
@Table(name = "authority")
public class Authority {

    @Target(AuthorityPK.class)
    @EmbeddedId
    private AuthorityPK id;

    @Column(name = "id")
    private UUID internalId;

    @JoinColumn(name = "authority_level")
    @ManyToOne
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

    public AuthorityDTO toDTO() {
        return new AuthorityDTO(
                this.id.getFkitSuperGroup().toDTO(),
                this.id.getPost().toDTO(),
                this.internalId,
                this.authorityLevel.toDTO()
                );
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
