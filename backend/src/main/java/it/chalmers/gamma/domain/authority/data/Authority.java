package it.chalmers.gamma.domain.authority.data;

import java.util.Objects;
import java.util.UUID;

import javax.persistence.*;

@Entity
@Table(name = "authority")
public class Authority {

    @EmbeddedId
    private AuthorityPK id;

    protected Authority() {}

    public Authority(AuthorityPK id) {
        this.id = id;
    }

    public AuthorityPK getId() {
        return id;
    }

}

