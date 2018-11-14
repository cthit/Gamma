package it.chalmers.gamma.db.entity;

import it.chalmers.gamma.db.entity.pk.AuthorityPK;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "authority")
public class Authority {
    @EmbeddedId
    AuthorityPK id;

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
