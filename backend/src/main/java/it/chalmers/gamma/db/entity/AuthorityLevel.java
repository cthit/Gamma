package it.chalmers.gamma.db.entity;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "authority_level")
public class AuthorityLevel implements GrantedAuthority {

    @Id
    @Column(updatable = false)
    private UUID id;

    @Column(name = "authority_level")
    private String authorityLevel;

    public AuthorityLevel(){
        id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAuthority() {
        return authorityLevel;
    }

    public void setAuthorityLevel(String authorityLevel) {
        this.authorityLevel = authorityLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthorityLevel that = (AuthorityLevel) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(authorityLevel, that.authorityLevel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, authorityLevel);
    }

    @Override
    public String toString() {
        return "AuthorityLevel{" +
                "id=" + id +
                ", authorityLevel='" + authorityLevel + '\'' +
                '}';
    }

}
