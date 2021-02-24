package it.chalmers.gamma.domain.authority.data.db;

import it.chalmers.gamma.domain.BaseEntity;
import it.chalmers.gamma.domain.authority.data.dto.AuthorityShallowDTO;

import javax.persistence.*;

@Entity
@Table(name = "authority")
public class Authority implements BaseEntity<AuthorityShallowDTO> {

    @EmbeddedId
    private AuthorityPK id;

    protected Authority() {}

    public Authority(AuthorityPK id) {
        this.id = id;
    }

    public AuthorityPK getId() {
        return id;
    }

    @Override
    public AuthorityShallowDTO toDTO() {
        return new AuthorityShallowDTO(
                id.getSuperGroupId(),
                id.getPostId(),
                id.getAuthorityLevelName()
        );
    }
}

