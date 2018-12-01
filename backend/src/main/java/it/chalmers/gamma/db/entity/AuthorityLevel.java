package it.chalmers.gamma.db.entity;

import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "authority_level")
@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "AvoidDuplicateLiterals"})

public class AuthorityLevel implements GrantedAuthority {

    @Id
    @Column(updatable = false)
    private UUID id;

    @Column(name = "authority_level")
    private String authorityLevel;

    public AuthorityLevel() {
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public String getAuthority() {
        return this.authorityLevel;
    }

    public void setAuthorityLevel(String authorityLevel) {
        this.authorityLevel = authorityLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AuthorityLevel that = (AuthorityLevel) o;
        return Objects.equals(this.id, that.id)
            && Objects.equals(this.authorityLevel, that.authorityLevel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.authorityLevel);
    }

    @Override
    public String toString() {
        return "AuthorityLevel{"
            + "id=" + this.id
            + ", authorityLevel='"
            + this.authorityLevel + '\''
            + '}';
    }

}
