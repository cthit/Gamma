package it.chalmers.gamma.db.entity;

import it.chalmers.gamma.db.entity.pk.AuthorityPK;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "authority")
public class Authority {
    @EmbeddedId
    AuthorityPK id;

    @Column(name = "id")
    private UUID internalId;

    @JoinColumn(name = "authority_level")
    @OneToOne
    private AuthorityLevel authorityLevel;

    public AuthorityPK getId() {
        return id;
    }

    public void setId(AuthorityPK id) {
        this.id = id;
    }

    public AuthorityLevel getAuthorityLevel() {
        return authorityLevel;
    }

    public void setAuthorityLevel(AuthorityLevel authorityLevel) {
        this.authorityLevel = authorityLevel;
    }

    public UUID getInternal_id() { return internalId; }

    public void setInternal_id(UUID internal_id) { this.internalId = internal_id; }

    public Authority(){
        this.internalId = UUID.randomUUID();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Authority authority = (Authority) o;
        return Objects.equals(id, authority.id) &&
                Objects.equals(authorityLevel, authority.authorityLevel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, authorityLevel);
    }

    @Override
    public String toString() {
        return "Authority{" +
                "id=" + id +
                ", authorityLevel=" + authorityLevel +
                '}';
    }
}
