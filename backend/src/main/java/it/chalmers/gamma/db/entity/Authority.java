package it.chalmers.gamma.db.entity;

import it.chalmers.gamma.db.entity.pk.AuthorityPK;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "authority")
public class Authority {
    public enum Authorities{
        USER(0), ADMIN(1);
        private int priority;
        Authorities(int priority){
        this.priority = priority;
    }
        public int getPriority(){
        return priority;
    }
    }
    @EmbeddedId
    AuthorityPK id;

    @Column(name = "authority_level")
    private Authorities authorityLevel;

    public AuthorityPK getId() {
        return id;
    }

    public void setId(AuthorityPK id) {
        this.id = id;
    }

    public Authorities getAuthorityLevel() {
        return authorityLevel;
    }

    public void setAuthorityLevel(Authorities authorityLevel) {
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
