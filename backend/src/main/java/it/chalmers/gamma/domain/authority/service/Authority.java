package it.chalmers.gamma.domain.authority.service;

import it.chalmers.gamma.util.domain.abstraction.BaseEntity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "authority")
public class Authority extends BaseEntity<AuthorityShallowDTO> {

    @EmbeddedId
    private AuthorityPK id;

    protected Authority() {}

    protected Authority(AuthorityShallowDTO authority) {
        this.id = new AuthorityPK(
                authority.getSuperGroupId(),
                authority.getPostId(),
                authority.getAuthorityLevelName()
        );
    }

    protected AuthorityPK getId() {
        return id;
    }

    @Override
    protected AuthorityShallowDTO toDTO() {
        return new AuthorityShallowDTO(
                id.getSuperGroupId(),
                id.getPostId(),
                id.getAuthorityLevelName()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Authority authority = (Authority) o;
        return Objects.equals(id, authority.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

