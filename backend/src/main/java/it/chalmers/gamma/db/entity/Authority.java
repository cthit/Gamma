package it.chalmers.gamma.db.entity;

import it.chalmers.gamma.domain.dto.authority.AuthorityDTO;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "authority")
public class Authority {

    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "fkit_group_id")
    private FKITSuperGroup fkitSuperGroup;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @JoinColumn(name = "authority_level")
    @ManyToOne
    private AuthorityLevel authorityLevel;

    public UUID getId() {
        return this.id;
    }

    public Authority() {
        id = UUID.randomUUID();
    }

    public AuthorityLevel getAuthorityLevel() {
        return this.authorityLevel;
    }

    public void setAuthorityLevel(AuthorityLevel authorityLevel) {
        this.authorityLevel = authorityLevel;
    }

    public AuthorityDTO toDTO() {
        return new AuthorityDTO(
                this.fkitSuperGroup.toDTO(),
                this.post.toDTO(),
                this.id,
                this.authorityLevel.toDTO()
                );
    }

    public void setFkitSuperGroup(FKITSuperGroup fkitSuperGroup) {
        this.fkitSuperGroup = fkitSuperGroup;
    }

    public void setPost(Post post) {
        this.post = post;
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
